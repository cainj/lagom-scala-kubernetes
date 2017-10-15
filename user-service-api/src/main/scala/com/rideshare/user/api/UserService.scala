package com.rideshare.user.api

import java.util.NoSuchElementException

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json, OFormat}

object UserService {
  val CreateUserTopic = "created_user"
}

/**
 * The user service interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the UserappService.
 */
trait UserService extends Service {

  def create(): ServiceCall[User, User]

  def get(id: String): ServiceCall[NotUsed, User]

  /**
   * This gets published to Kafka.
   */
  def createdUserTopic(): Topic[User]

  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("userservice")
      .withCalls(
        restCall(Method.POST, "/api/user/create", create _),
        restCall(Method.GET, "/api/user/:id", get _)
      )
      .withTopics(
        topic(UserService.CreateUserTopic, createdUserTopic _)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // name as the partition key.
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[User](_.id.getOrElse(throw new NoSuchElementException("User must have a id")))
          )
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

/**
 * The user
 * @param id The UUID
 * @param email  The email
 * @param mobilePhone The mobilePhone
 *
 */
case class User(
  id: Option[String],
  email: Option[String],
  mobilePhone: String
)

object User {

  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[User] = Json.format[User]

  val serializers: OFormat[User] = Json.format[User]

}

