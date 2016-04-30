package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Post.schema ++ Site.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Post
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param userid Database column userId SqlType(TEXT)
   *  @param comment Database column comment SqlType(TEXT)
   *  @param siteid Database column siteId SqlType(INT)
   *  @param created Database column created SqlType(DATETIME) */
  case class PostRow(id: Int, userid: String, comment: String, siteid: Int, created: java.sql.Timestamp)
  /** GetResult implicit for fetching PostRow objects using plain SQL queries */
  implicit def GetResultPostRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[PostRow] = GR{
    prs => import prs._
    PostRow.tupled((<<[Int], <<[String], <<[String], <<[Int], <<[java.sql.Timestamp]))
  }
  /** Table description of table post. Objects of this class serve as prototypes for rows in queries. */
  class Post(_tableTag: Tag) extends Table[PostRow](_tableTag, "post") {
    def * = (id, userid, comment, siteid, created) <> (PostRow.tupled, PostRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userid), Rep.Some(comment), Rep.Some(siteid), Rep.Some(created)).shaped.<>({r=>import r._; _1.map(_=> PostRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column userId SqlType(TEXT) */
    val userid: Rep[String] = column[String]("userId")
    /** Database column comment SqlType(TEXT) */
    val comment: Rep[String] = column[String]("comment")
    /** Database column siteId SqlType(INT) */
    val siteid: Rep[Int] = column[Int]("siteId")
    /** Database column created SqlType(DATETIME) */
    val created: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created")
  }
  /** Collection-like TableQuery object for table Post */
  lazy val Post = new TableQuery(tag => new Post(tag))

  /** Entity class storing rows of table Site
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param url Database column url SqlType(TEXT)
   *  @param sitetitle Database column siteTitle SqlType(TEXT)
   *  @param created Database column created SqlType(DATETIME) */
  case class SiteRow(id: Int, url: String, sitetitle: String, created: java.sql.Timestamp)
  /** GetResult implicit for fetching SiteRow objects using plain SQL queries */
  implicit def GetResultSiteRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[SiteRow] = GR{
    prs => import prs._
    SiteRow.tupled((<<[Int], <<[String], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table site. Objects of this class serve as prototypes for rows in queries. */
  class Site(_tableTag: Tag) extends Table[SiteRow](_tableTag, "site") {
    def * = (id, url, sitetitle, created) <> (SiteRow.tupled, SiteRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(url), Rep.Some(sitetitle), Rep.Some(created)).shaped.<>({r=>import r._; _1.map(_=> SiteRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column url SqlType(TEXT) */
    val url: Rep[String] = column[String]("url")
    /** Database column siteTitle SqlType(TEXT) */
    val sitetitle: Rep[String] = column[String]("siteTitle")
    /** Database column created SqlType(DATETIME) */
    val created: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created")
  }
  /** Collection-like TableQuery object for table Site */
  lazy val Site = new TableQuery(tag => new Site(tag))
}
