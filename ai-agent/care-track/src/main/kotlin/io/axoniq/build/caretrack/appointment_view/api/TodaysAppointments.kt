package io.axoniq.build.caretrack.appointment_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "TodaysAppointments",
  namespace = "caretrack",
)
public data class TodaysAppointments(
  public val doctorId: String,
)
