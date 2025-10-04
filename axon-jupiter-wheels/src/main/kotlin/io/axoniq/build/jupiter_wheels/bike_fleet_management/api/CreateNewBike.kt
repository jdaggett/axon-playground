package io.axoniq.build.jupiter_wheels.bike_fleet_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "CreateNewBike",
  namespace = "jupiter-wheels",
)
public data class CreateNewBike(
  public val location: String,
  public val bikeType: String,
  public val condition: String,
)
