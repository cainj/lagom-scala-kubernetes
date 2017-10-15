package com.rideshare.user.impl

import com.rideshare.user.api.UserService
import com.lightbend.lagom.internal.client.CircuitBreakerMetricsProviderImpl
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.client.LagomServiceClientComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.dns.DnsServiceLocatorComponents

class UserLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new UserApplication(context) with KubernetesRuntimeComponents {
      override lazy val circuitBreakerMetricsProvider = new CircuitBreakerMetricsProviderImpl(actorSystem)
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new UserApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[UserService]
  )
}

sealed trait KubernetesRuntimeComponents extends LagomServiceClientComponents with DnsServiceLocatorComponents

abstract class UserComponents(context: LagomApplicationContext)
    extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[UserService](wire[UserServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: UserSerializerRegistry.type = UserSerializerRegistry

  // Register the athlete persistent entity
  persistentEntityRegistry.register(wire[UserEntity])

  //Register the readside for an athlete
  readSide.register(wire[UserReadSideProcessor])
}

abstract class UserApplication(context: LagomApplicationContext) extends UserComponents(context)
  with LagomKafkaComponents