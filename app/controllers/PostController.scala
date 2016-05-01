package controllers

import java.sql.Timestamp
import javax.inject.Inject
import controllers.helpers.ControllerHelper
import models.PostDAO
import models.Tables.PostRow
import play.api.mvc.{Action, Controller}

/**
  * Created by taishi on 2016/04/30.
  */
class PostController @Inject()(val postDAO: PostDAO) extends Controller with ControllerHelper{
  def messagePost(url: String) = Action { request =>
    val ip: String = request.remoteAddress
    val postMessage: String = request.body.asFormUrlEncoded.get("postMessage").mkString
    val url: String = addHttp(request.body.asFormUrlEncoded.get("url").mkString)
    val siteId: Int = request.body.asFormUrlEncoded.get("siteId").mkString.toInt
    val postAction: Boolean = postDAO.post(PostRow(0, ip, postMessage, siteId, new Timestamp(System.currentTimeMillis())))
    if (postAction) {
      Redirect(s"http://localhost:9000/$url")
    } else {
      BadRequest(views.html.index(url + " : badRequest . def post"))
    }
  }
}
