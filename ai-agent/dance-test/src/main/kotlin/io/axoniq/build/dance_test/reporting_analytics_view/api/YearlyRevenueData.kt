package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Double
import kotlin.collections.List

public data class YearlyRevenueData(
  public val yearlyComparisons: List<YearlyRevenue>,
  public val growthRate: Double,
)
