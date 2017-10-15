package com.rideshare.user.impl

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import com.rideshare.user.api.{User, UserService}

/**
 * Implementation of the [[UserService]].
 */
class UserServiceImpl(
    persistentEntityRegistry: PersistentEntityRegistry,
    cassandraSession: CassandraSession
) extends UserService {

  def get(id: String): ServiceCall[NotUsed, User] = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[UserEntity](id)
    ref.ask(GetUser)
  }

  override def create(): ServiceCall[User, User] = ServiceCall { request =>
    val uuid = UUID.randomUUID()
    val ref = persistentEntityRegistry.refFor[UserEntity](request.mobilePhone)
    ref.ask(CreateUser(request.copy(Some(uuid.toString))))
  }

  override def createdUserTopic(): Topic[User] =
    TopicProducer.taggedStreamWithOffset[User, UserEvent](UserEvent.Tag.allTags.toList) {
      (aggregate, offset) =>
        persistentEntityRegistry.eventStream(aggregate, offset)
          .map(event => (convertEvent(event), offset))
    }

  private def convertEvent(eventStreamElement: EventStreamElement[UserEvent]): User = {
    eventStreamElement.event match {
      case UserAdded(_, user) => user
      case x => throw new UnsupportedOperationException(s"Does not export a $x")
    }
  }
}

