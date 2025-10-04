package io.axoniq.build.jupiter_wheels.rental_extension_management.api

import kotlin.Int
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RequestRentalExtension",
  namespace = "jupiter-wheels",
)
public data class RequestRentalExtension(
  public val additionalTime: Int,
  @TargetEntityId
  public val rentalId: String,
)
