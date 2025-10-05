package io.axoniq.build.dance_test.instructor_profile_view.api

import kotlin.String
import kotlin.collections.List
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "InstructorProfileCreated",
  namespace = "dance-test",
)
public data class InstructorProfileCreated(
  public val email: String,
  public val specialties: List<String>,
  @EventTag(key = "Instructor")
  public val instructorId: String,
  public val phone: String,
)
