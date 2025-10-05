package io.axoniq.build.dance_test.calendly_integration.api

import java.time.LocalDateTime
import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "SessionDetailsModified",
  namespace = "dance-test",
)
public data class SessionDetailsModified(
  public val newDuration: Int?,
  public val newSessionDate: LocalDateTime?,
  @EventTag(key = "Session")
  public val sessionId: String,
)
