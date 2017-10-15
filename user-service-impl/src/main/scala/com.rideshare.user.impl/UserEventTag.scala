package com.trainerapp.athlete.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}

class UserEventTag {

  object AthleteEvent {
    val UserEventTag = AggregateEventTag[AthleteEvent]

  }

  //#aggregate-tag
  /**
   * This interface defines all the events that the UserEntity supports.
   */
  sealed trait AthleteEvent extends AggregateEvent[AthleteEvent] {
    override def aggregateTag: AggregateEventTag[AthleteEvent] =
      AthleteEvent.UserEventTag
  }
}

