package io.axoniq.build.caretrack.appointment_view.api

import kotlin.collections.List

public data class PatientUpcomingAppointmentsResult(
  public val appointments: List<AppointmentInfo>,
)
