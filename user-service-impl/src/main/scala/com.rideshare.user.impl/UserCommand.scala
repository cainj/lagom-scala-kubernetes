package com.rideshare.user.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import com.rideshare.user.api.User

/**
 * This interface defines all the commands that the Athlete entity supports.
 */
sealed trait UserCommand[R] extends ReplyType[R]

object UserCommand {
  import JsonSerializer.emptySingletonFormat

  import play.api.libs.json._
  val serializers = Vector(
    JsonSerializer(Json.format[CreateUser]),
    JsonSerializer(emptySingletonFormat(GetUser)),
    JsonSerializer(Json.format[PatchUser])
  )
}

case class CreateUser(user: User) extends UserCommand[User]

case class PatchUser(user: User) extends UserCommand[Done]

case object GetUser extends UserCommand[User]
