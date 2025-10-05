package io.axoniq.build.caretrack.medical_history_view.api

import java.time.LocalDate
import kotlin.String

public data class DiagnosisDetailsResult(
  public val doctorName: String,
  public val severity: String,
  public val notes: String?,
  public val diagnosisId: String,
  public val condition: String,
  public val diagnosisDate: LocalDate,
)
