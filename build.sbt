name := "toy-search-engine"

version := "0.1.0"

scalaVersion := "2.11.6"

organization := "com.github.sorhus"

libraryDependencies ++= {
  Seq(
  )
}

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases" at "http://oss.sonatype.org/content/repositories/releases"
)
 
scalacOptions ++= Seq("-unchecked", "-deprecation")

