
import play.sbt.Play
import sbt._

  object Dependencies {

    object Authentication {
      private[this] val version = "5.0.0"
      val silhouette: ModuleID = "com.mohiva" %% "play-silhouette" % version
      val hasher: ModuleID = "com.mohiva" %% "play-silhouette-password-bcrypt" % version
      val persistence: ModuleID = "com.mohiva" %% "play-silhouette-persistence" % version
      val crypto: ModuleID = "com.mohiva" %% "play-silhouette-crypto-jca" % version
      val authySDK = "com.twilio.sdk" % "twilio" % "7.7.0"
    }

    object GraphQL {
      val sangria: ModuleID = "org.sangria-graphql" %% "sangria" % "1.3.0"
      val playJson: ModuleID = "org.sangria-graphql" %% "sangria-play-json" % "1.0.3"
      val circe: ModuleID = "org.sangria-graphql" %% "sangria-circe" % "1.1.0"
    }

    object Serialization {
      val circeVersion = "0.8.0"
    }


    object Search{
      val elastic4sVersion = "5.4.1"
      val elasticSearch = "com.sksamuel.elastic4s" %% "elastic4s-tcp" % elastic4sVersion
      val elasticSearchStream = "com.sksamuel.elastic4s" %% "elastic4s-streams" % elastic4sVersion

    }

    object Utils {
      val scapegoatVersion = "1.3.1"
      val enumeratumVersion = "1.5.14"

      val csv: ModuleID = "com.github.tototoshi" %% "scala-csv" % "1.3.5"
      val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
      val bCrypt = "org.mindrot" % "jbcrypt" % "0.3"
      val serviceLocator = "com.lightbend" %% "lagom14-scala-service-locator-dns" % "2.2.2"
    }

    object MyPlay {
      val version = "2.6.0"
      val jodaJson: ModuleID = "com.typesafe.play" %% "play-json-joda" % version
      val ws: ModuleID = Play.autoImport.ws
    }


    object Testing {
      val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.4" % "test"
    }
  }

