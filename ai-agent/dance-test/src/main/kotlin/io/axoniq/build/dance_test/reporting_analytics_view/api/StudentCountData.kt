package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.String
import kotlin.collections.List

public data class StudentCountData(
  public val monthlyStudentCounts: List<MonthlyStudentCount>,
  public val growthTrend: String,
)
