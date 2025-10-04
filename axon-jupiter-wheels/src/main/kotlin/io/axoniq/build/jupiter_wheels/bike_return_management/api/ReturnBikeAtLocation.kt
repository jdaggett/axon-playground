package io.axoniq.build.jupiter_wheels.bike_return_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ReturnBikeAtLocation",
  namespace = "jupiter-wheels",
)
public data class ReturnBikeAtLocation(
  public val returnLocation: String,
  @TargetEntityId
  public val rentalId: String,
)
