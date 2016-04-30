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
import org.jsoup.{Connection, Jsoup}

case class LatelyPost(comment: String, siteUrl: String, siteTitle: String)
case class Comment(comment: String, created: Timestamp)

class Application @Inject()(val postDAO: PostDAO) extends Controller {

  private val displayCount = 5
  private val showAllDisplayCount = 3
  private val urlForm = Form("url" -> text)
  private val postForm = Form("postMessage" -> text)

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
      Redirect(s"http://localhost:9000/$url")
    } else {
      BadRequest(views.html.index(url + " : badRequest . def post"))
    }
  }

  def showAll(urlArg: String) = Action {

    val url = addHttp(urlArg)
    val title: Option[String] = postDAO.getSiteTitle(url)
    val siteId: Option[Int] = postDAO.getSiteId(url)
    if (List(title, siteId).map(_.isDefined).forall(_ == true)) {
      val postAll:Seq[Tables.PostRow] = postDAO.getPost(siteId.get, showAllDisplayCount)
      Ok(views.html.commentList(url, title.get, siteId.get, postAll, postForm))
    } else {
      BadRequest(views.html.index(url + " : badRequest . def post"))
    }
  }

  def show(urlArg: String) = Action {
    val url = addHttp(urlArg)
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
