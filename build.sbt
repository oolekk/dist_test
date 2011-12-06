name := "distense webapp"

version := "1.0"

scalaVersion := "2.9.1"
 
seq(webSettings: _*)

scalacOptions ++= Seq("-unchecked", "-deprecation")

seq(ProguardPlugin.proguardSettings :_*)

// If using JRebel with 0.1.0 of the sbt web plugin
//jettyScanDirs := Nil
// using 0.2.4+ of the sbt web plugin
scanDirectories in Compile := Nil

//resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

// you can also add multiple repositories at the same time
resolvers ++= Seq(
  "Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
  "Scala Tools Snapshot" at "http://scala-tools.org/repo-snapshots/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
  "OpenNMS - needet for fast-md5" at "http://repo.opennms.org/maven2/"
)

// if you have issues pulling dependencies from the scala-tools repositories (checksums don't match), you can disable checksums
//checksums := Nil

libraryDependencies ++= {
  val liftVersion = "2.4-M5" // Put the current/latest lift version here
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    //"net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-widgets" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-squeryl-record" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-wizard" % liftVersion % "compile->default",
    "mysql" % "mysql-connector-java" % "5.1.18" % "compile->default",
    //"postgresql" % "postgresql" % "9.0-801.jdbc4" % "compile->default",
    "com.jolbox" % "bonecp" % "0.7.1.RELEASE" % "compile->default",
    //"org.mindrot" % "jbcrypt" % "0.3m" % "compile->default",
    "org.im4java" % "im4java" % "1.2.0" % "compile->default",
    "fast-md5" % "fast-md5" % "2.6.1" % "compile->default"
    )
}

// when using the sbt web app plugin 0.2.4+, use "container" instead of "jetty" for the context
// Customize any further dependencies as desired
libraryDependencies ++= Seq(
  //"org.apache.tomcat" % "jasper" % "6.0.33" % "provided->default",
  "org.eclipse.jetty" % "jetty-webapp" % "8.0.4.v20111024" % "container", // For Jetty 8
  //"org.eclipse.jetty" % "jetty-webapp" % "7.3.0.v20110203" % "container", // For Jetty 7
  //"org.mortbay.jetty" % "jetty" % "6.1.22" % "jetty,test", // For Jetty 6, add scope test to make jetty avl. for tests
  "org.scala-tools.testing" % "specs_2.9.0" % "1.6.8" % "test", // For specs.org tests
  "junit" % "junit" % "4.8" % "test->default", // For JUnit 4 testing
  "javax.servlet" % "servlet-api" % "2.5" % "provided->default",
  "com.h2database" % "h2" % "1.3.161", // In-process database, useful for development systems
  "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default" // Logging
)

