package io.axoniq.build.dance_test.session_booking.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "SessionScheduledBeyondLimit",
  namespace = "dance-test",
)
public data class SessionScheduledBeyondLimit(
  public val studentId: String,
  @EventTag(key = "Session")
  public val sessionId: String,
)
