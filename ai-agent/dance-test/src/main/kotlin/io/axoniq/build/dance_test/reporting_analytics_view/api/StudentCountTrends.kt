package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Int
import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "StudentCountTrends",
  namespace = "dance-test",
)
public data class StudentCountTrends(
  public val instructorId: String,
  public val periodMonths: Int,
)
