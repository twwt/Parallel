package models

import java.sql.Timestamp

import models.Tables.PostRow
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._
import play.api.libs.json._

// Combinator syntax

/**
  * Created by taishi on 2016/05/01.
  */
case class PostRowJson(id: Int, userid: String, comment: String, siteid: Int, created: DateTime)

object PostRowJson extends ((Int, String, String, Int, DateTime) => PostRowJson) {

  //  implicit val timestampFormat = new Format[Timestamp] {
  //
  //    def writes(t: Timestamp): JsValue = toJson(timestampToDateTime(t))
  //
  //    def reads(json: JsValue): JsResult[Timestamp] = fromJson[DateTime](json).map(dateTimeToTimestamp)
  //
  //  }
  //  implicit val PostRowReads = (
  //    (__ \ "id").read[Int] ~
  //      (__ \ "userId").read[String] ~
  //      (__ \ "comment").read[String] ~
  //      (__ \ "siteid").read[Int] ~
  //      (__ \ "created").read[DateTime].map[Timestamp](t => new Timestamp(t.getMillis))
  //    ) (PostRowJson)
  //
  //  implicit val PostRowWrites = (
  //    (__ \ "id").write[Int] ~
  //      (__ \ "userId").write[String] ~
  //      (__ \ "comment").write[String] ~
  //      (__ \ "siteid").write[Int] ~
  //      (__ \ "created").write[DateTime].contramap[Timestamp](t => new DateTime(t.getTime))
  //    ) (unlift(PostRowJson.unapply))

  implicit val PostRowWrites = (
    (__ \ "id").format[Int] ~
      (__ \ "userId").format[String] ~
      (__ \ "comment").format[String] ~
      (__ \ "siteid").format[Int] ~
      (__ \ "created").format[DateTime]
    ) (PostRowJson, unlift(PostRowJson.unapply))
  //  val sampleLatelyPostJsonJson = Json.obj(
  //    "full_name" -> "Nico Yazawa",
  //    "age" -> 17
  //  )

  //  val person = Json.fromJson[LatelyPostJson](sampleLatelyPostJsonJson).get
  //  print(person.age) // -> 17
}

