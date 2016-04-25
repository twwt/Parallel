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
  lazy val schema: profile.SchemaDescription = Post.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Post
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param postid Database column postId SqlType(TEXT)
   *  @param postmessage Database column postMessage SqlType(TEXT)
   *  @param siteurl Database column siteUrl SqlType(TEXT)
   *  @param created Database column created SqlType(DATETIME) */
  case class PostRow(id: Int, postid: String, postmessage: String, siteurl: String, created: java.sql.Timestamp)
  /** GetResult implicit for fetching PostRow objects using plain SQL queries */
  implicit def GetResultPostRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[PostRow] = GR{
    prs => import prs._
    PostRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table post. Objects of this class serve as prototypes for rows in queries. */
  class Post(_tableTag: Tag) extends Table[PostRow](_tableTag, "post") {
    def * = (id, postid, postmessage, siteurl, created) <> (PostRow.tupled, PostRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(postid), Rep.Some(postmessage), Rep.Some(siteurl), Rep.Some(created)).shaped.<>({r=>import r._; _1.map(_=> PostRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column postId SqlType(TEXT) */
    val postid: Rep[String] = column[String]("postId")
    /** Database column postMessage SqlType(TEXT) */
    val postmessage: Rep[String] = column[String]("postMessage")
    /** Database column siteUrl SqlType(TEXT) */
    val siteurl: Rep[String] = column[String]("siteUrl")
    /** Database column created SqlType(DATETIME) */
    val created: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created")
  }
  /** Collection-like TableQuery object for table Post */
  lazy val Post = new TableQuery(tag => new Post(tag))
}
