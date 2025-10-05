package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Double
import kotlin.Int
import kotlin.collections.List

public data class YearlyRevenue(
  public val monthlyBreakdown: List<MonthlyRevenue>,
  public val year: Int,
  public val totalRevenue: Double,
)
