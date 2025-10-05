package io.axoniq.build.dance_test.communication_management.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "WaitingListCreated",
  namespace = "dance-test",
)
public data class WaitingListCreated(
  public val creationDate: LocalDateTime,
  @EventTag(key = "Instructor")
  public val instructorId: String,
)
