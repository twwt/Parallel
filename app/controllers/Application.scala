package controllers

import java.sql.Timestamp
import javax.inject.Inject

import models.Tables.PostRow
import models.{PostDAO, Tables}
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.collection.immutable.Range
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex
import org.jsoup.{Connection, Jsoup}

//case class Post(userId: String, postMessage: String)

//@Singleton
class Application @Inject()(val postDAO: PostDAO) extends Controller {

  val urlForm = Form("url" -> text)
  val postForm = Form("postMessage" -> text)

  def index = Action { request =>
    Ok(views.html.index(request.body.asText.get))
  }

  def input = Action {
    Ok(views.html.input(urlForm))
  }

  def addHttp(url: String): String = {
    url match {
      case url if (url.contains("http")) => url
      case url if (!url.contains("http")) => "http://" + url
    }
  }

  def getConnectAutoClose(url: String, dom: String): Option[List[String]] = {
    val connect: Connection = Jsoup.connect(url).timeout(2000).ignoreHttpErrors(true).followRedirects(true)
    val statusCode: Option[Int] = Try(connect.execute().statusCode()).toOption
    statusCode.flatMap {
      case statusCode if (statusCode >= 200 && statusCode < 300 || statusCode == 304) =>
        val elems = connect.get.select(dom)
        val elemSize = elems.size()
        Some((for (index <- Range(0, elemSize)) yield elems.get(index).text()).toList)
      case _ => None
    }
  }

  def post(url: String) = Action { request =>
    val ip: String = request.remoteAddress
    val postMessage: String = request.body.asFormUrlEncoded.get("postMessage").mkString
    val url: String = addHttp(request.body.asFormUrlEncoded.get("url").mkString)
    postDAO.post(PostRow(0, ip, postMessage, url, new Timestamp(System.currentTimeMillis()))).onComplete {
      case Success(msg) =>
        val postAll = postDAO.findAll(url)
        Ok(views.html.siteBbs(url, postAll, postForm))
      case Failure(e) => BadRequest(views.html.index(url + " : badRequest . errorCode => " + e))
    }
    val postAll = postDAO.findAll(url)
    Ok(views.html.siteBbs(url, postAll, postForm))
  }

  def show(urlArg: String) = Action { request =>
    val url = addHttp(urlArg)
    val postAll = postDAO.findAll(url)
    val title = getConnectAutoClose(url, "title").getOrElse(s"$url のタイトルが取得できませんでした。")
    println(title)
    Ok(views.html.siteBbs(url, postAll, postForm))
  }

  def urlShow(url: String) = Action { request =>
    Ok(views.html.frame(s"http://$url"))
  }

  def submit = Action { implicit request =>
    val url = urlForm.bindFromRequest.get
    Ok(views.html.frame(url))
  }

}
