package io.axoniq.build.jupiter_wheels.bike_return_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "SubmitBikePhoto",
  namespace = "jupiter-wheels",
)
public data class SubmitBikePhoto(
  public val photoUrl: String,
  @TargetEntityId
  public val rentalId: String,
)
