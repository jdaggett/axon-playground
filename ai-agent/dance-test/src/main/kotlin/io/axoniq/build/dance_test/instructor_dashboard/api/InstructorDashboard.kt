package io.axoniq.build.dance_test.instructor_dashboard.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "InstructorDashboard",
  namespace = "dance-test",
)
public data class InstructorDashboard(
  public val instructorId: String,
)
