package io.axoniq.build.caretrack.appointment_view.api

import java.time.LocalDateTime
import kotlin.String

public data class AppointmentDetailsResult(
  public val doctorName: String,
  public val purpose: String,
  public val patientName: String,
  public val appointmentDate: LocalDateTime,
  public val status: String,
  public val appointmentId: String,
)
