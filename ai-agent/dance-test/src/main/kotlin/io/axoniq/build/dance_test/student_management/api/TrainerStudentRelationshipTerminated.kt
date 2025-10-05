package io.axoniq.build.dance_test.student_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "TrainerStudentRelationshipTerminated",
  namespace = "dance-test",
)
public data class TrainerStudentRelationshipTerminated(
  public val instructorId: String,
  @EventTag(key = "Student")
  public val studentId: String,
)
