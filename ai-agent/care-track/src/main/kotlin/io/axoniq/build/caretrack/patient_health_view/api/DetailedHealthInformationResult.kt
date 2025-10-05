package io.axoniq.build.caretrack.patient_health_view.api

import kotlin.String

public data class DetailedHealthInformationResult(
  public val detailedInfo: String,
  public val healthArea: String,
)
