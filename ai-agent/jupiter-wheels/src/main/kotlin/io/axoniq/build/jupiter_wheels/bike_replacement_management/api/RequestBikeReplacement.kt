package io.axoniq.build.jupiter_wheels.bike_replacement_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RequestBikeReplacement",
  namespace = "jupiter-wheels",
)
public data class RequestBikeReplacement(
  @TargetEntityId
  public val originalBikeId: String,
  public val rentalId: String,
  public val issueDescription: String,
)
