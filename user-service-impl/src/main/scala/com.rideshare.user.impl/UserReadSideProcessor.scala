package com.rideshare.user.impl

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

/**
 * The Read Side processing for athletes
 *
 * @param session Cassandra session
 * @param readSide Cassandra ReadSide utility
 */
class UserReadSideProcessor(session: CassandraSession, readSide: CassandraReadSide) extends ReadSideProcessor[UserEvent] {

  override def aggregateTags: Set[AggregateEventTag[UserEvent]] = UserEvent.Tag.allTags

  //#prepare-statements
  private val writeAthletePromise = Promise[PreparedStatement] // initialized in prepare
  private def writeUser: Future[PreparedStatement] = writeAthletePromise.future

  private def createTable(): Future[Done] = session.executeCreateTable(
    "CREATE TABLE IF NOT EXISTS user (" +
      "id text, " +
      "mobilePhone text, " +
      "PRIMARY KEY (mobilePhone))"
  )

  private def prepareInsertUser(): Future[Done] = {
    val f = session.prepare("INSERT INTO user (id, mobilePhone) " +
      "VALUES (?, ?)")
    writeAthletePromise.completeWith(f)
    f.map(_ => Done)
  }

  private def processUserAdded(eventElement: EventStreamElement[UserAdded]): Future[List[BoundStatement]] = {
    writeUser.map { ps =>
      val bindWriteAthlete = ps.bind()
      bindWriteAthlete.setString("id", eventElement.event.entityId)
      bindWriteAthlete.setString("mobilePhone", eventElement.event.user.mobilePhone)
      List(bindWriteAthlete)
    }
  }

  private def processUserPatched(eventElement: EventStreamElement[UserPatched]): Future[List[BoundStatement]] = {
    writeUser.map { ps =>
      val bindUser = ps.bind()
      bindUser.setString("id", eventElement.event.entityId)
      bindUser.setString("mobilePhone", eventElement.event.user.mobilePhone)
      List(bindUser)
    }
  }

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[UserEvent] = {
    //#create-builder
    val builder = readSide.builder[UserEvent]("useroffset")
    //#create-builder

    //#register-global-prepare
    builder.setGlobalPrepare(createTable)
    //#register-global-prepare

    //#register-prepare
    builder.setPrepare(tag => prepareInsertUser())
    //#register-prepare

    //#set-event-handler
    builder.setEventHandler[UserAdded](processUserAdded)
    builder.setEventHandler[UserPatched](processUserPatched)
    //#set-event-handler

    //#build
    builder.build()
    //#build
  }
}
