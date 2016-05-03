package controllers

import java.sql.Timestamp
import javax.inject.Inject

import controllers.helpers.ControllerHelper
import models.Tables.{PostRow, SiteRow}
import models.{PostDAO, PostRowJson, Tables}
import org.joda.time.DateTime
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.{JsValue, Json}

import scalaz._
import Scalaz._
import scala.util.Try


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

  def show(urlArg: String) = Action { implicit request =>
    val url = addHttp(request.uri.tail)
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

  def getDiffPost = Action { implicit request =>
    val postRowJson: Option[List[Tables.PostRow]] = for {
      siteId <- request.queryString.get("siteId").map(_.mkString(""))
      posts <- Try(postDAO.getPost(siteId.toInt, 0, displayCount)).toOption
    } yield {
      posts.toList
    }
    postRowJson match {
      case Some(jsonList) =>
        val postsJson:List[JsValue] = jsonList.map(posts => Json.toJson(PostRowJson(posts.id, posts.userid, posts.comment, posts.siteid, new DateTime(posts.created.getTime))))
        Ok(views.html.json(postsJson.toString))
      case None => BadRequest(views.html.json("none"))
    }
    //    val jsonValue: Option[JsValue] = request.queryString.get("data").map(_.mkString("")) match {
    //      case Some(json) if json.nonEmpty => Json.parse(json).some
    //      case Some(json) if json.isEmpty => None
    //      case _ => None
    //    }
    //    val latelyPostJson: Option[LatelyPostJson] = jsonValue.map(jv => Json.fromJson[LatelyPostJson](jv).getOrElse(LatelyPostJson(0,List(0))))
    //    latelyPostJson match {
    //      case Some(latelyPostJson) => Ok(views.html.json(latelyPostJson.postIds.mkString("")))
    //      case None => Ok(views.html.json("miss"))
    //    }
//    Ok(views.html.json(postRowJson.toString))
  }

  def urlShow(url: String) = Action { request =>
    Ok(views.html.frame(s"http://$url"))
  }

  def submit = Action { implicit request =>
    val url = urlForm.bindFromRequest.get
    Ok(views.html.frame(url))
  }

}
