package com.rideshare.user.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import com.rideshare.user.api.User

/**
 * This interface defines all the events that the Athlete entity supports.
 */
sealed trait UserEvent extends AggregateEvent[UserEvent] {
  override def aggregateTag: AggregateEventShards[UserEvent] = UserEvent.Tag
}

object UserEvent {
  val NumShards = 3
  // second param is optional, defaults to the class name
  val Tag = AggregateEventTag.sharded[UserEvent](NumShards)

  import play.api.libs.json._

  val serializers = Vector(
    JsonSerializer(Json.format[UserAdded]),
    JsonSerializer(Json.format[AddUser]),
    JsonSerializer(Json.format[UserPatched])
  )
}

case class UserAdded(entityId: String, user: User) extends UserEvent

case class UserPatched(entityId: String, user: User) extends UserEvent

case class AddUser(user: User) extends UserEvent

case class FindUsers() extends UserEvent