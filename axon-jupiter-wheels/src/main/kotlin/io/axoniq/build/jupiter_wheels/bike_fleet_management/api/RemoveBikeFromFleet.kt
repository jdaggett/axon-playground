package io.axoniq.build.jupiter_wheels.bike_fleet_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RemoveBikeFromFleet",
  namespace = "jupiter-wheels",
)
public data class RemoveBikeFromFleet(
  public val removalReason: String,
  @TargetEntityId
  public val bikeId: String,
)
