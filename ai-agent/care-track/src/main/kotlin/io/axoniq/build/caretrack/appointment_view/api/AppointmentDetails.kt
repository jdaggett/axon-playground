package io.axoniq.build.caretrack.appointment_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "AppointmentDetails",
  namespace = "caretrack",
)
public data class AppointmentDetails(
  public val appointmentId: String,
)
