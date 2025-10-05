package io.axoniq.build.dance_test.instructor_dashboard.api

import java.time.LocalDateTime
import kotlin.String

public data class ActivitySummary(
  public val activityType: String,
  public val activityDate: LocalDateTime,
  public val studentName: String,
)
