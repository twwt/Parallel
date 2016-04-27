package models


import javax.inject.Inject

import models.Tables.Post
import models.Tables.PostRow
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by taishi on 2016/04/26.
  */

class PostDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val posts = TableQuery[Post]

  def findAll(siteUrl: String): Seq[Tables.PostRow] = {
    val postAll = db.run(posts.filter(_.siteurl === siteUrl).result)
    Await.result(postAll, Duration.Inf)
  }

  def post(postRow: PostRow) = {
    db.run(posts += postRow)
  }
}
