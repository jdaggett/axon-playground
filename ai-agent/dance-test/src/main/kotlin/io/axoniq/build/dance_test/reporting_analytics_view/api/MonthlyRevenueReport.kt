package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Int
import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "MonthlyRevenueReport",
  namespace = "dance-test",
)
public data class MonthlyRevenueReport(
  public val instructorId: String,
  public val month: Int,
  public val year: Int,
)
