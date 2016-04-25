package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.libs.mailer.{AttachmentData, Email, MailerPlugin}
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

case class Post(userId: String, postMessage: String)

object Application extends Controller {

  val urlForm = Form("url" -> text)
  val postForm = Form("postMessage" -> text)

  def index = Action { request =>
    Ok(views.html.index(request.body.asText.get))
  }

  def input = Action {
    Ok(views.html.input(urlForm))
  }


  def post(url: String) = Action { request =>
    val ip: String = request.remoteAddress
    val postMessage: String = request.body.asFormUrlEncoded.get("postMessage").mkString
    val url: String = request.body.asFormUrlEncoded.get("url").mkString
    val post: Post = Post(ip, postMessage)
    Ok(views.html.siteBbs(url, post, postForm))
  }

  def show(url: String) = Action { request =>
    val ip: String = "request.remoteAddress"
    val postMessage: String = "request.body.asFormUrlEncoded.get().mkString"
    val post: Post = Post(ip, postMessage)
    Ok(views.html.siteBbs(url, post, postForm))
  }

  def urlShow(url: String) = Action { request =>
    Ok(views.html.frame(s"http://$url"))
  }

  def submit = Action { implicit request =>
    val url = urlForm.bindFromRequest.get
    Ok(views.html.frame(url))
  }

}
