package io.axoniq.build.dance_test.instructor_dashboard.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "OutstandingBalancesDashboard",
  namespace = "dance-test",
)
public data class OutstandingBalancesDashboard(
  public val instructorId: String,
)
