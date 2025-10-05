package io.axoniq.build.caretrack.medical_history_view.api

import kotlin.String

public data class TreatmentDetailsResult(
  public val frequency: String,
  public val prescribingDoctorName: String,
  public val dosage: String,
  public val medicationName: String,
  public val duration: String,
  public val treatmentId: String,
)
