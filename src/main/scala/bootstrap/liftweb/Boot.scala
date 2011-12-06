package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._

import _root_.net.liftweb.db.{DB, ConnectionManager, ConnectionIdentifier, DefaultConnectionIdentifier, StandardDBVendor}
import _root_.java.sql.{Connection, DriverManager}
// local imports
import _root_.com.distense.model._

// db connection pool provider
import com.jolbox.bonecp.BoneCP
import com.jolbox.bonecp.BoneCPConfig

import net.liftweb.squerylrecord.SquerylRecord
import org.squeryl.{ SessionFactory, Session, Schema }
import org.squeryl.adapters.H2Adapter
import org.squeryl.adapters.MySQLAdapter
//import org.squeryl.adapters.PostgreSqlAdapter

// session wrapper imports
import net.liftweb.util.LoanWrapper
import net.liftweb.squerylrecord.RecordTypeMode._

class Boot extends Loggable{

	/* Force the request to be UTF-8 */
	private def makeUtf8(req: HTTPRequest) { req.setCharacterEncoding("UTF-8") }
	
	/* Build SiteMap */
	private val entries = List(
		Menu(Loc("home", Link(List("index"), false, "index"), "HOME",
			LocGroup("public")))
		) ::: User.menus

	def boot {

		/* default package to search for snippets, views, comet */
		LiftRules.addToPackages("com.distense")
		/* explicit snippet dispatching */
		LiftRules.snippetDispatch.append {
			// default placeholder snippet
			case _ => com.distense.snippet.NotFoundSnippet
		} 

		/* generate HTML5 documents as output from internal XHTML templates */
		LiftRules.htmlProperties.default.set((r: Req) => new XHtmlInHtml5OutProperties(r.userAgent))
		/* Force the request to be UTF-8 */
		LiftRules.early.append(makeUtf8)
		/* set the sitemap.  If you don't want access control for each page, comment this out */
		LiftRules.setSiteMap(SiteMap(entries:_*))

		/* use jQuery framework */
		LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts
		/* Show the spinny image when an Ajax call starts */
		LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
		/* Make the spinny image go away when it ends */
		LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

		/* store uploads as files on disk */
		LiftRules.handleMimeFile = OnDiskFileParamHolder.apply
		/* set max upload size */
		LiftRules.maxMimeSize = 1024 * 1024 * 10
		LiftRules.maxMimeFileSize = LiftRules.maxMimeSize

		/* print to the console in which mode this lift application is currently running */
		println("RUNNING in " +
			(if(Props.modeName == "") "DEV" else (Props.modeName)).toUpperCase + " MODE")
		
		/* make h2 db console browser-accessible in dev mode */
		if(Props.modeName == ""){
			// pass paths that start with 'console' to be processed by the H2Console servlet
			println("Set up H2 db console")
			LiftRules.liftRequest.append({case r if
				(r.path.partPath match {case "console" :: _ => true case _ => false}) => false})
		}
		
		/* squeryl setup and session wrap-around */
		SquerylRecord.initWithSquerylSession(PoolProvider.getSession)
		SessionFactory.concreteFactory = Some(()=>PoolProvider.getSession)        
		S.addAround(new LoanWrapper{override def apply[T](f: => T): T = {inTransaction{f}}})
			
		/* recreate db schema anew if db.schemify == true */
		if(Props.getBool("db.schemify", false)){
			println("Running Schemifier - drop and recreate db tables")
			inTransaction{
				MySchema.drop
				MySchema.create
				
				// create one super-user for testing
				val root = User.createRecord.login("root").pass("fake1").
					firstName("Olek").lastName("Bolek").superUser(true)
				MySchema.users.insert(root)
				
				// create one regular-user for testing
				val guest = User.createRecord.login("guest").pass("fake2").
					firstName("Foo").lastName("Bar").superUser(false)
				MySchema.users.insert(guest)
			}
		}
		
	}
}

/* database connection pooling provider - we are using BoneCP */
object PoolProvider{

	var pool : Box[BoneCP] = Empty

	try {
		// load the DB driver class
		Class.forName(Props.get("db.driver").openTheBox)
		// create a new configuration object	
		val config = new BoneCPConfig
		// set the JDBC url
		config.setJdbcUrl(Props.get("db.url").openTheBox)
		// set the username
		config.setUsername(Props.get("db.user").openOr(""))
		println("db.user: "+Props.get("db.user").openOr(""))
		// set the password
		config.setPassword(Props.get("db.pass").openOr(""))
		println("db.pass: "+Props.get("db.pass").openOr(""))
		// setup the connection pool
		pool = Full(new BoneCP(config))
		println("BoneCP connection pool is now initialized.")
	} catch {
		case e : Exception => {
			println("BoneCP - FAILED to initialize connection pool.")
			e.printStackTrace
		}
	}

	def getConnection : Connection = {
		pool.openTheBox.getConnection
	}
	
	def getSession : Session = {
		println("BoneCP get new session")
		Session.create(
			getConnection,
			(if(Props.modeName == "") new H2Adapter else new MySQLAdapter)
		)
	}

}
