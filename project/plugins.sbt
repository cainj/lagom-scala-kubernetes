// The Lagom plugin
addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.4.0-M1")

// Needed for importing the project into Eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.1.0")

// The Cinnamon Telemetry plugin
addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.4.3")

resolvers += Resolver.bintrayRepo("hajile", "maven")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0") // scalariformFormat

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")