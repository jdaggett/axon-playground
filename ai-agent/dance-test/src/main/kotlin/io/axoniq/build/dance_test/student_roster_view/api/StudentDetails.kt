package io.axoniq.build.dance_test.student_roster_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "StudentDetails",
  namespace = "dance-test",
)
public data class StudentDetails(
  public val studentId: String,
)
