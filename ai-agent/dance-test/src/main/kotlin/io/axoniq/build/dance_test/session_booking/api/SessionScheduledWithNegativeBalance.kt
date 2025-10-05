package io.axoniq.build.dance_test.session_booking.api

import kotlin.Double
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "SessionScheduledWithNegativeBalance",
  namespace = "dance-test",
)
public data class SessionScheduledWithNegativeBalance(
  public val studentId: String,
  public val negativeBalance: Double,
  @EventTag(key = "Session")
  public val sessionId: String,
)
