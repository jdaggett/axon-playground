package io.axoniq.build.caretrack.family_health_view.api

import java.time.LocalDate
import kotlin.String

public data class DiagnosisSummary(
  public val condition: String,
  public val diagnosisDate: LocalDate,
)
