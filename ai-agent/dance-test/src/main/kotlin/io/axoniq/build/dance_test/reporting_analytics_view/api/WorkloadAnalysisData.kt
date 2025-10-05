package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class WorkloadAnalysisData(
  public val capacityUtilization: Double,
  public val averageSessionsPerDay: Double,
  public val totalSessionsPerWeek: Double,
  public val peakDays: List<String>,
  public val availableCapacity: Int,
)
