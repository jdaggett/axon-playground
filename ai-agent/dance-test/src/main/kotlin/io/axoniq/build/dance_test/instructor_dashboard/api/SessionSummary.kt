package io.axoniq.build.dance_test.instructor_dashboard.api

import java.time.LocalDateTime
import kotlin.String

public data class SessionSummary(
  public val sessionDate: LocalDateTime,
  public val sessionId: String,
  public val studentName: String,
)
