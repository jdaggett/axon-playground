package io.axoniq.build.dance_test.session_booking.api

import kotlin.Boolean
import kotlin.String

public data class SessionBookingResult(
  public val success: Boolean,
  public val sessionId: String,
)
