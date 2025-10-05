package io.axoniq.build.caretrack.medical_record_management.api

import kotlin.Boolean
import kotlin.String

public data class TreatmentPrescriptionResult(
  public val treatmentPrescribed: Boolean,
  public val treatmentId: String,
)
