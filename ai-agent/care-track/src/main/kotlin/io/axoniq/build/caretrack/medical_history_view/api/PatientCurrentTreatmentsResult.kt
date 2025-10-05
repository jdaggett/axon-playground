package io.axoniq.build.caretrack.medical_history_view.api

import kotlin.collections.List

public data class PatientCurrentTreatmentsResult(
  public val treatments: List<TreatmentInfo>,
)
