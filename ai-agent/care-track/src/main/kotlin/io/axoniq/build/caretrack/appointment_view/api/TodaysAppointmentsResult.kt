package io.axoniq.build.caretrack.appointment_view.api

import kotlin.collections.List

public data class TodaysAppointmentsResult(
  public val appointments: List<TodayAppointmentInfo>,
)
