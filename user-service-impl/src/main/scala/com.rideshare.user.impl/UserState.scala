package com.rideshare.user.impl

import com.rideshare.user.api.User
import play.api.libs.json.{Format, Json}

/**
 * The handles the state of an athlete
 */
object UserState {
  /**
   * Format for the athlete state.
   *
   * Persisted entities get snapshotted every configured number of events. This
   * means the state gets stored to the database, so that when the entity gets
   * loaded, you don't need to replay all the events, just the ones since the
   * snapshot. Hence, a JSON format needs to be declared so that it can be
   * serialized and deserialized when storing to and from the database.
   */
  implicit val format: Format[UserState] = Json.format

  def empty: UserState = UserState(None)

}

/**
 * The current state held by the persistent entity.
 */
case class UserState(user: Option[User]) {

  def isEmpty: Boolean = user.isEmpty

}
