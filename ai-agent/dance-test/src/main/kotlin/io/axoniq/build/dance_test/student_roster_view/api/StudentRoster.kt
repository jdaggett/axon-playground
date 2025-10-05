package io.axoniq.build.dance_test.student_roster_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "StudentRoster",
  namespace = "dance-test",
)
public data class StudentRoster(
  public val instructorId: String,
)
