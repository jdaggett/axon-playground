package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.String

public data class BikeItem(
  public val location: String,
  public val bikeType: String,
  public val status: String,
  public val bikeId: String,
)
