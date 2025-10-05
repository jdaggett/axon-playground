package io.axoniq.build.dance_test.student_roster_view.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "LessonBalanceDecreasedFromSession",
  namespace = "dance-test",
)
public data class LessonBalanceDecreasedFromSession(
  public val lessonsUsed: Int,
  @EventTag(key = "Student")
  public val studentId: String,
  public val sessionId: String,
)
