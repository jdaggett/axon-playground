package io.axoniq.build.caretrack.appointment_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "PatientUpcomingAppointments",
  namespace = "caretrack",
)
public data class PatientUpcomingAppointments(
  public val patientId: String,
)
