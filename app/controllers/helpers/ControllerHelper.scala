package controllers.helpers

import org.jsoup.{Connection, Jsoup}
import scala.collection.immutable.Range
import scala.util.Try

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
    connect.flatMap { c =>
      c.execute().statusCode() match {
        case statusCode if (statusCode >= 200 && statusCode < 300 || statusCode == 304) =>
          val elems = c.get.select(targetDom)
          val elemSize = elems.size()
          Some((for (index <- Range(0, elemSize)) yield elems.get(index).text()).toList)
        case _ => None
      }
    }
  }

  def fetchTitle(urlArg: String): String = {
    fetchTargetHtml(urlArg, "title").map(_ (0)).getOrElse(s"$urlArg のタイトルが取得できませんでした。")
  }
}
