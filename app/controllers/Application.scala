package controllers

import java.sql.Timestamp
import javax.inject.Inject

import controllers.helpers.ControllerHelper
import models.Tables.{PostRow, SiteRow}
import models.{PostDAO, Tables}
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

case class LatelyPost(comment: String, siteUrl: String, siteTitle: String)

case class Comment(comment: String, created: Timestamp)

class Application @Inject()(val postDAO: PostDAO) extends Controller with ControllerHelper {

  private val displayCount = 2
  private val showAllDisplayCount = 2
  private val urlForm = Form("url" -> text)
  private val postForm = Form("postMessage" -> text)

  def index = Action { request =>
    Ok(views.html.index("index"))
  }

  def input = Action {
    Ok(views.html.input(urlForm))
  }

  def showAll(urlArg: String, page: Int) = Action {
    val url = addHttp(urlArg)
    val title: Option[String] = postDAO.getSiteTitle(url)
    val siteId: Option[Int] = postDAO.getSiteId(url)
    if (List(title, siteId).map(_.isDefined).forall(_ == true)) {
      val postAll: Seq[Tables.PostRow] = postDAO.getPost(siteId.get, ((showAllDisplayCount * page) - showAllDisplayCount), showAllDisplayCount)
      Ok(views.html.commentList(url, title.get, siteId.get, postAll, page, postForm))
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
    val startRange = 0
    val postAll = postDAO.getPost(siteId, startRange, displayCount)
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
