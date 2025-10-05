package io.axoniq.build.caretrack.appointment_view.api

import java.time.LocalDateTime
import kotlin.String

public data class AppointmentInfo(
  public val doctorName: String,
  public val purpose: String,
  public val appointmentDate: LocalDateTime,
  public val appointmentId: String,
)
