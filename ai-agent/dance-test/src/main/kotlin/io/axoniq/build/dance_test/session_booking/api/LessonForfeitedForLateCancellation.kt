package io.axoniq.build.dance_test.session_booking.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "LessonForfeitedForLateCancellation",
  namespace = "dance-test",
)
public data class LessonForfeitedForLateCancellation(
  public val lessonsForfeited: Int,
  @EventTag(key = "Student")
  public val studentId: String,
  public val sessionId: String,
)
