package io.axoniq.build.dance_test.instructor_dashboard.api

import java.time.LocalDateTime
import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "SessionScheduled",
  namespace = "dance-test",
)
public data class SessionScheduled(
  public val instructorId: String,
  public val duration: Int,
  public val sessionDate: LocalDateTime,
  public val studentId: String,
  @EventTag(key = "Session")
  public val sessionId: String,
)
