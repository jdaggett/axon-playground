package io.axoniq.build.jupiter_wheels.bike_rental_management.api

import kotlin.String

public data class BikeRentalRequestResult(
  public val rentalId: String,
  public val bikeId: String,
)
