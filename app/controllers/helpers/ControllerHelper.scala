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
    val ua = """Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36"""
    val connect: Option[Connection] = Try(Jsoup.connect(url).userAgent(ua).timeout(2000).referrer("http://www.google.com").ignoreContentType(true).ignoreHttpErrors(true).followRedirects(true)).toOption
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
    fetchTargetHtml(urlArg, "title").map(_ (0)).getOrElse(s"$urlArg のタイトルは取得できませんでした。")
  }
}
