package io.axoniq.build.dance_test.student_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "StudentProfileDeleted",
  namespace = "dance-test",
)
public data class StudentProfileDeleted(
  @EventTag(key = "Student")
  public val studentId: String,
)
