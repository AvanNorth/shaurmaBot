import sbt._

object Dependencies {
  val scalatest = "org.scalatest" %% "scalatest" % "3.2.15" % "test"
  val cats = "org.typelevel" %% "cats-effect" % "3.4.8"

  val telegramium: Seq[ModuleID] = Seq(
    "io.github.apimorphism" %% "telegramium-core" % "7.66.0",
    "io.github.apimorphism" %% "telegramium-high" % "7.66.0"
  )

  val http4s = "org.http4s" %% "http4s-ember-server" % "0.23.18"
  val estatico =  "io.estatico" %% "newtype" % "0.4.4"

  val doobie: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
    "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC2", // HikariCP transactor.
    "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC2" // Postgres driver 42.3.1 + type mappings.
  )
}