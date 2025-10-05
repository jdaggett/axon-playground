package io.axoniq.build.caretrack.medical_history_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DiagnosisDetails",
  namespace = "caretrack",
)
public data class DiagnosisDetails(
  public val diagnosisId: String,
)
