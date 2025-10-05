package io.axoniq.build.caretrack.medical_history_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "PatientMedicalHistory",
  namespace = "caretrack",
)
public data class PatientMedicalHistory(
  public val patientId: String,
)
