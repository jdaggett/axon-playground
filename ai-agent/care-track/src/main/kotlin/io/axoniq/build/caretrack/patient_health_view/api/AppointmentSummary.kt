package io.axoniq.build.caretrack.patient_health_view.api

import java.time.LocalDateTime
import kotlin.String

public data class AppointmentSummary(
  public val doctorName: String,
  public val appointmentDate: LocalDateTime,
)
