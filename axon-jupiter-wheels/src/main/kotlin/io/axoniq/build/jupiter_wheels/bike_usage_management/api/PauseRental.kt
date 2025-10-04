package io.axoniq.build.jupiter_wheels.bike_usage_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "PauseRental",
  namespace = "jupiter-wheels",
)
public data class PauseRental(
  @TargetEntityId
  public val rentalId: String,
)
