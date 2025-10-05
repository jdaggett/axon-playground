package io.axoniq.build.dance_test.session_calendar_view.api

import java.time.LocalDateTime
import kotlin.Int
import kotlin.String

public data class SessionDetailsData(
  public val duration: Int,
  public val sessionDate: LocalDateTime,
  public val studentId: String,
  public val notes: String?,
  public val status: String,
  public val sessionId: String,
  public val studentName: String,
  public val actualDuration: Int?,
)
