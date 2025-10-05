package io.axoniq.build.dance_test.student_roster_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DetailedStudentInformation",
  namespace = "dance-test",
)
public data class DetailedStudentInformation(
  public val instructorId: String,
  public val studentId: String,
)
