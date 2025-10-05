package io.axoniq.build.caretrack.medical_history_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "TreatmentDetails",
  namespace = "caretrack",
)
public data class TreatmentDetails(
  public val treatmentId: String,
)
