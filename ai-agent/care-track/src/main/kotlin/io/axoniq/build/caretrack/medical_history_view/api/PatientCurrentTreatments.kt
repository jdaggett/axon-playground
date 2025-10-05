package io.axoniq.build.caretrack.medical_history_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "PatientCurrentTreatments",
  namespace = "caretrack",
)
public data class PatientCurrentTreatments(
  public val patientId: String,
)
