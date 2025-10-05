package io.axoniq.build.dance_test.instructor_dashboard.api

import java.time.LocalDateTime
import kotlin.String

public data class BookingNotification(
  public val sessionDate: LocalDateTime,
  public val notificationDate: LocalDateTime,
  public val sessionId: String,
  public val studentName: String,
)
