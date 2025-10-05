package io.axoniq.build.caretrack.appointment_management.api

import kotlin.Boolean
import kotlin.String

public data class AppointmentSchedulingResult(
  public val appointmentScheduled: Boolean,
  public val appointmentId: String,
)
