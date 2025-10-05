package io.axoniq.build.dance_test.instructor_dashboard.api

import java.time.LocalDateTime
import kotlin.String

public data class ActivityItem(
  public val activityType: String,
  public val description: String,
  public val activityDate: LocalDateTime,
  public val studentName: String,
)
