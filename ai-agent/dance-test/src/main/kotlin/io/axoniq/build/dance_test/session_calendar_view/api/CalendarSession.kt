package io.axoniq.build.dance_test.session_calendar_view.api

import java.time.LocalDateTime
import kotlin.Int
import kotlin.String

public data class CalendarSession(
  public val duration: Int,
  public val sessionDate: LocalDateTime,
  public val status: String,
  public val sessionId: String,
  public val studentName: String,
)
