package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Double
import kotlin.String
import kotlin.collections.List

public data class WeeklyPattern(
  public val averageSessions: Double,
  public val dayOfWeek: String,
  public val peakHours: List<String>,
)
