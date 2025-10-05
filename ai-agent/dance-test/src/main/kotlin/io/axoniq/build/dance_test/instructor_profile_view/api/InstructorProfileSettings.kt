package io.axoniq.build.dance_test.instructor_profile_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "InstructorProfileSettings",
  namespace = "dance-test",
)
public data class InstructorProfileSettings(
  public val instructorId: String,
)
