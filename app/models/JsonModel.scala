package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

// Combinator syntax

/**
  * Created by taishi on 2016/05/01.
  */
case class LatelyPostJson(siteId: Int, postIds: List[Int])

object LatelyPostJson extends ((Int, List[Int]) => LatelyPostJson) {
  implicit val LatelyPostJsonReads = (
    (__ \ "siteId").read[Int] ~
      (__ \ "postIds").read[List[Int]]
    ) (LatelyPostJson)
  //  val sampleLatelyPostJsonJson = Json.obj(
  //    "full_name" -> "Nico Yazawa",
  //    "age" -> 17
  //  )

  //  val person = Json.fromJson[LatelyPostJson](sampleLatelyPostJsonJson).get
  //  print(person.age) // -> 17
}

