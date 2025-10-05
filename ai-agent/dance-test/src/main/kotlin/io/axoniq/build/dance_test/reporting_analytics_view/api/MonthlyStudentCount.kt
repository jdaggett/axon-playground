package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Int
import kotlin.String

public data class MonthlyStudentCount(
  public val newStudents: Int,
  public val month: String,
  public val activeStudents: Int,
)
