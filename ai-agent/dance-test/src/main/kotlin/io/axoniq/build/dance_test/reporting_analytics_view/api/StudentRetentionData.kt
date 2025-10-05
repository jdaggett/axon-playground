package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Double
import kotlin.Int

public data class StudentRetentionData(
  public val newStudents: Int,
  public val averageSessionsPerStudent: Double,
  public val retentionRate: Double,
  public val churredStudents: Int,
  public val activeStudents: Int,
)
