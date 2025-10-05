package io.axoniq.build.caretrack.patient_health_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "PersonalHealthDashboard",
  namespace = "caretrack",
)
public data class PersonalHealthDashboard(
  public val patientId: String,
)
