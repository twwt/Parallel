package models

import play.api.libs.json.Json

/**
  * Created by taishi on 2016/05/01.
  */
case class PostIdJson(siteId: Int)
object PostIdJson {
  implicit def jsonWrites = Json.writes[PostIdJson]
  implicit def jsonReads = Json.reads[PostIdJson]
}
