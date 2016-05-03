package models

import java.sql.Timestamp

import models.Tables.PostRow
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

// Combinator syntax

/**
  * Created by taishi on 2016/05/01.
  */
case class PostRowJson(id: Int, userid: String, comment: String, siteid: Int, created: DateTime)

object PostRowJson extends ((Int, String, String, Int, DateTime) => PostRowJson) {
  implicit val PostRowReads = (
    (__ \ "id").read[Int] ~
      (__ \ "userId").read[String] ~
      (__ \ "comment").read[String] ~
      (__ \ "siteid").read[Int] ~
      (__ \ "created").read[DateTime]
    ) (PostRowJson)

  implicit val PostRowWrites = (
    (__ \ "id").write[Int] ~
      (__ \ "userId").write[String] ~
      (__ \ "comment").write[String] ~
      (__ \ "siteid").write[Int] ~
      (__ \ "created").write[DateTime]
    ) (unlift(PostRowJson.unapply))
  //  val sampleLatelyPostJsonJson = Json.obj(
  //    "full_name" -> "Nico Yazawa",
  //    "age" -> 17
  //  )

  //  val person = Json.fromJson[LatelyPostJson](sampleLatelyPostJsonJson).get
  //  print(person.age) // -> 17
}

