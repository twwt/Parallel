package models


import java.security.Timestamp
import javax.inject.Inject

import controllers.LatelyPost
import models.Tables.{Post, PostRow, Site, SiteRow}
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.collection.JavaConverters._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.Try

/**
  * Created by taishi on 2016/04/26.
  */

class PostDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val postQuery = TableQuery[Post]
  val siteQuery = TableQuery[Site]

  def findPostAll(siteId: Int): Seq[Tables.PostRow] = {
    val postAll = db.run(postQuery.filter(_.siteid === siteId).result)
    Await.result(postAll, Duration.Inf)
  }

  def getPost(siteId: Int, startRange: Int, endRange: Int): Seq[Tables.PostRow] = {
    val posts = db.run(postQuery.filter(_.siteid === siteId).filter(_.siteid.between(startRange, endRange)).result)
    Await.result(posts, Duration.Inf)
  }

  def getSiteId(siteUrl: String): Option[Int] = {
    val siteId: Future[Option[Int]] = db.run(siteQuery.filter(_.url === siteUrl).map(_.id).result.headOption)
    Await.result(siteId, Duration.Inf)
  }

  def getSiteTitle(urlArg: String): Option[String] = {
    val siteTitle: Future[Option[String]] = db.run(siteQuery.filter(_.url === urlArg).map(_.sitetitle).result.headOption)
    Await.result(siteTitle, Duration.Inf)
  }

  def post(postRow: PostRow): Boolean = {
    val f = db.run(postQuery += postRow)
    Await.result(f, Duration.Inf)
    f.isCompleted
  }

  def getLatelyPosts(): Seq[LatelyPost] = {
    val query = for {
      (p, s) <- postQuery join siteQuery on (_.siteid === _.id)
    } yield (p.comment, s.url, s.sitetitle)
    Await.result(db.run(query.result), Duration.Inf).map(LatelyPost.tupled(_))
  }

  def addSite(siteRow: SiteRow): Int = {
    val insertQuery = siteQuery returning siteQuery.map(_.id) into ((site, id) => site.copy(id = id))
    val action = insertQuery += siteRow
    Await.result(db.run(action), Duration.Inf).id
  }
}