package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.Double
import kotlin.String

public data class BikeDetailsResult(
  public val location: String,
  public val bikeType: String,
  public val userRating: Double?,
  public val condition: String,
  public val bikeId: String,
)
