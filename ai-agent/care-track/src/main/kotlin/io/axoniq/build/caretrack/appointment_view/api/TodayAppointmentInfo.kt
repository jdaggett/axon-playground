package io.axoniq.build.caretrack.appointment_view.api

import java.time.LocalDateTime
import kotlin.String

public data class TodayAppointmentInfo(
  public val purpose: String,
  public val patientName: String,
  public val appointmentTime: LocalDateTime,
  public val appointmentId: String,
)
