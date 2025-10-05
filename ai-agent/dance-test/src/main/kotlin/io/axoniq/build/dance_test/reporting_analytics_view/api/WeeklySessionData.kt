package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Int
import kotlin.collections.List

public data class WeeklySessionData(
  public val totalWeeklySessions: Int,
  public val weeklyPatterns: List<WeeklyPattern>,
)
