package io.axoniq.build.dance_test.instructor_dashboard.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "RecentStudentActivity",
  namespace = "dance-test",
)
public data class RecentStudentActivity(
  public val instructorId: String,
)
