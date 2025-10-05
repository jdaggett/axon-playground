package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.String

public data class ReplacementBikeDetailsResult(
  public val location: String,
  public val bikeType: String,
  public val condition: String,
  public val bikeId: String,
)
