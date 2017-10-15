import Dependencies._
import com.typesafe.sbt.packager.Keys.dockerBaseImage
import com.typesafe.sbt.packager.docker._

organization in ThisBuild := "com.rideshare"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"
lagomCassandraCleanOnStart in ThisBuild := true

val commonSettings = lagomForkedTestSettings ++ scalariformSettings

lazy val cinnamonDependencies = Seq(
  // Use Coda Hale Metrics and Lagom instrumentation
  Cinnamon.library.cinnamonCHMetrics,
  Cinnamon.library.cinnamonLagom,
  Cinnamon.library.cinnamonOpenTracingZipkin,
  Cinnamon.library.cinnamonOpenTracingZipkinKafka,
  Cinnamon.library.cinnamonOpenTracingZipkinScribe
)

lazy val `ride-share-app` = (project in file("."))
  .enablePlugins(DockerPlugin)
  .aggregate(`user-service-api`,
    `user-service-impl`)



lazy val `user-service-api` = (project in file("user-service-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    ) ++ cinnamonDependencies
  )

lazy val `user-service-impl` = (project in file("user-service-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      Utils.serviceLocator,
      Utils.macwire,
      Testing.scalaTest
    ),
    dockerRepository := Some("docker.io/cainj"),
    dockerBaseImage := "openjdk:8-jdk-slim",
    dockerUpdateLatest := true,
    dockerEntrypoint ++= """-Dlagom.broker.kafka.brokers=${DEFAULT_KAFKA_BROKERS:-local:host:9093} -Dplay.http.secret.key="${APPLICATION_SECRET:-none}" -Dplay.akka.actor-system="${AKKA_ACTOR_SYSTEM_NAME:-userservice-v1}" -Dhttp.address="$USERSERVICE_BIND_IP" -Dhttp.port="$USERSERVICE_BIND_PORT" -Dakka.actor.provider=cluster -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" -Dakka.remote.netty.tcp.port="$AKKA_REMOTING_BIND_PORT" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://$AKKA_ACTOR_SYSTEM_NAME@$NODE"; I=$(expr $I + 1); done) -Dakka.io.dns.resolver=async-dns -Dakka.io.dns.async-dns.resolve-srv=true -Dakka.io.dns.async-dns.resolv-conf=on""".split(" ").toSeq,
    dockerCommands :=
    dockerCommands.value.flatMap {
      case ExecCmd("ENTRYPOINT", args @ _*) => Seq(Cmd("ENTRYPOINT", args.mkString(" ")))
      case v => Seq(v)
    }
  )
  .settings(commonSettings: _*)
  .dependsOn(`user-service-api`)
