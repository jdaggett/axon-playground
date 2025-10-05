package io.axoniq.build.dance_test.student_roster_view.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "LessonBalanceIncreasedFromPackage",
  namespace = "dance-test",
)
public data class LessonBalanceIncreasedFromPackage(
  public val lessonCount: Int,
  public val packageId: String,
  @EventTag(key = "Student")
  public val studentId: String,
)
