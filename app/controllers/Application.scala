package controllers

import java.sql.Timestamp
import javax.inject.Inject

import models.Tables.{PostRow, SiteRow}
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

import scala.concurrent.Await
import scala.concurrent.duration.Duration

//case class Post(userId: String, postMessage: String)
class Application @Inject()(val postDAO: PostDAO) extends Controller {

  val displayCount = 5
  val urlForm = Form("url" -> text)
  val postForm = Form("postMessage" -> text)

  def index = Action { request =>
    Ok(views.html.index("index"))
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

  def fetchTargetHtml(url: String, targetDom: String): Option[List[String]] = {
    val connect: Option[Connection] = Try(Jsoup.connect(url).timeout(2000).ignoreHttpErrors(true).followRedirects(true)).toOption
    connect.flatMap { c =>
      c.execute().statusCode() match {
        case statusCode if (statusCode >= 200 && statusCode < 300 || statusCode == 304) =>
          val elems = c.get.select(targetDom)
          val elemSize = elems.size()
          Some((for (index <- Range(0, elemSize)) yield elems.get(index).text()).toList)
        case _ => None
      }
    }
  }

  def fetchTitle(urlArg: String): String = {
    fetchTargetHtml(urlArg, "title").map(_ (0)).getOrElse(s"$urlArg のタイトルが取得できませんでした。")
  }

  def messagePost(url: String) = Action { request =>
    val ip: String = request.remoteAddress
    val postMessage: String = request.body.asFormUrlEncoded.get("postMessage").mkString
    val url: String = addHttp(request.body.asFormUrlEncoded.get("url").mkString)
    val siteId: Int = request.body.asFormUrlEncoded.get("siteId").mkString.toInt
    val siteTitle = postDAO.getSiteTitle(url).getOrElse(fetchTitle(url))
    val postAction: Boolean = postDAO.post(PostRow(0, ip, postMessage, siteId, new Timestamp(System.currentTimeMillis())))
    if (postAction) {
      val postAll = postDAO.findPostAll(siteId)
      val latelyPosts = postDAO.getLatelyPosts
      Ok(views.html.siteBbs(url, siteTitle, siteId, postAll, latelyPosts, postForm))
    } else {
      BadRequest(views.html.index(url + " : badRequest . def post"))
    }
  }

  /*todo titleがないときにだけ取得したい
  getSiteTitleするためにはidが必要
  */
  def show(urlArg: String) = Action {
    val url = addHttp(urlArg)
    postDAO.getSiteTitle(url)
    val title: String = postDAO.getSiteTitle(url).getOrElse(fetchTitle(url))
    val siteId = postDAO.getSiteId(url) match {
      case Some(siteId) => siteId
      case None => postDAO.addSite(SiteRow(0, url, title, new Timestamp(System.currentTimeMillis())))
    }
    val postAll = postDAO.getPost(siteId, displayCount)
    val latelyPosts = postDAO.getLatelyPosts
    Ok(views.html.siteBbs(url, title, siteId, postAll, latelyPosts, postForm))
  }

  def urlShow(url: String) = Action { request =>
    Ok(views.html.frame(s"http://$url"))
  }

  def submit = Action { implicit request =>
    val url = urlForm.bindFromRequest.get
    Ok(views.html.frame(url))
  }

}
