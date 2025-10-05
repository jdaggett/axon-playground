package io.axoniq.build.caretrack.medical_history_view.api

import kotlin.collections.List

public data class PatientMedicalHistoryResult(
  public val diagnoses: List<DiagnosisInfo>,
)
