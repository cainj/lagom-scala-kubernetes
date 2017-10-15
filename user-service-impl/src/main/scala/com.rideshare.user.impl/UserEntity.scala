package com.rideshare.user.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.rideshare.user.api.User

import scala.collection.immutable.Seq

/**
 * This is an event sourced entity. It has a state, [[UserState]], which
 * stores the athlete information.
 *
 * Commands get translated to events, and it's the events that get persisted by
 * the entity. Each event will have an event handler registered for it, and an
 * event handler simply applies an event to the current state. This will be done
 * when the event is first created, and it will also be done when the entity is
 * loaded from the database - each event will be replayed to recreate the state
 * of the entity.
 *
 */
class UserEntity extends PersistentEntity {

  override type Command = UserCommand[_]
  override type Event = UserEvent
  override type State = UserState

  /**
   * The initial state. This is used if there is no snapshotted state to be found.
   */
  override def initialState: UserState = UserState.empty

  /**
   * An entity can define different behaviours for different states, so the behaviour
   * is a function of the current state to a set of actions.
   */
  override def behavior: Behavior = {
    case state if state.isEmpty => initial
    case state if !state.isEmpty => userAdded
  }

  def initial: Actions =
    Actions().onCommand[CreateUser, User] {
      case (CreateUser(user), ctx, _) =>
        ctx.thenPersist(UserAdded(entityId, user)) { evt =>
          // After persist is done additional side effects can be performed
          ctx.reply(evt.user)
        }
    }.onEvent {
      case (UserAdded(id, athlete), state) => UserState(Some(athlete))
    }.onReadOnlyCommand[GetUser.type, User] {
      case (GetUser, ctx, state) =>
        ctx.reply(state.user.get)
    }

  def userAdded: Actions =
    Actions().onCommand[PatchUser, Done] {
      case (PatchUser(user), ctx, _) =>
        ctx.thenPersist(UserPatched(entityId, user))(_ => ctx.reply(Done))
    }.onEvent {
      case (UserPatched(id, user), _) => UserState(Option(user.copy(id = Option(id))))
    }.onReadOnlyCommand[GetUser.type, User] {
      case (GetUser, ctx, state) =>
        ctx.reply(state.user.get)
    }
}

/**
 * Akka serialization, used by both persistence and remoting, needs to have
 * serializers registered for every type serialized or deserialized. While it's
 * possible to use any serializer you want for Akka messages, out of the box
 * Lagom provides support for JSON, via this registry abstraction.
 *
 * The serializers are registered here, and then provided to Lagom in the
 * application loader.
 */
object UserSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[UserState],
    JsonSerializer[User]
  ) ++ UserEvent.serializers ++ UserCommand.serializers
}
