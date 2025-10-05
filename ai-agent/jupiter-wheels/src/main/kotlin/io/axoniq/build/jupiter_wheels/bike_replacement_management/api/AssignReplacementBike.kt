package io.axoniq.build.jupiter_wheels.bike_replacement_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "AssignReplacementBike",
  namespace = "jupiter-wheels",
)
public data class AssignReplacementBike(
  public val replacementBikeId: String,
  @TargetEntityId
  public val rentalId: String,
)
