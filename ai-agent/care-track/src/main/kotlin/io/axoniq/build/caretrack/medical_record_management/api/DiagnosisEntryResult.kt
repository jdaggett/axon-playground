package io.axoniq.build.caretrack.medical_record_management.api

import kotlin.Boolean
import kotlin.String

public data class DiagnosisEntryResult(
  public val diagnosisRecorded: Boolean,
  public val diagnosisId: String,
)
