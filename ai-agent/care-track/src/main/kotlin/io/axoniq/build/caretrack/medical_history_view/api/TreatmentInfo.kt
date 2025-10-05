package io.axoniq.build.caretrack.medical_history_view.api

import kotlin.String

public data class TreatmentInfo(
  public val frequency: String,
  public val dosage: String,
  public val medicationName: String,
  public val treatmentId: String,
)
