package io.axoniq.build.caretrack.patient_health_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DetailedHealthInformation",
  namespace = "caretrack",
)
public data class DetailedHealthInformation(
  public val patientId: String,
  public val healthArea: String,
)
