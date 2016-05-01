package controllers.helpers

import org.jsoup.{Connection, Jsoup}
import scala.collection.immutable.Range
import scala.util.Try
import scalaz._
import Scalaz._

/**
  * Created by taishi on 2016/04/30.
  */
trait ControllerHelper {
  def addHttp(url: String): String = {
    url match {
      case url if (url.contains("http")) => url
      case url if (!url.contains("http")) => "http://" + url
    }
  }


  def fetchTargetHtml(url: String, targetDom: String): Option[List[String]] = {
    val connect: Option[Connection] = Try(Jsoup.connect(url).timeout(2000).ignoreHttpErrors(true).followRedirects(true)).toOption
    println(connect)
    val statusCode: Option[Int] = connect match {
      case Some(c) => Try(c.execute().statusCode()).toOption
      case None => None
    }
    statusCode.flatMap { s =>
      if (s >= 200 && s < 300 || s == 304) {
        for {
          c <- connect
          elems = Try(c.get.select(targetDom)).toOption
          list <- Try(Range(0, elems.get.size()).toList).toOption
        } yield {
          list.flatMap(index => elems.map(_.get(index).text()))
        }
      } else {
        None
      }
    }
  }

  def fetchTitle(urlArg: String): String = {
    fetchTargetHtml(urlArg, "title").map(_ (0)).getOrElse(s"$urlArg のタイトルが取得できませんでした。")
  }
}
