package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Int
import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "YearlyRevenueComparison",
  namespace = "dance-test",
)
public data class YearlyRevenueComparison(
  public val instructorId: String,
  public val endYear: Int,
  public val startYear: Int,
)
