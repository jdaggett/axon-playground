package io.axoniq.build.jupiter_wheels.bike_usage_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "EndRentalEarlyDueToProblem",
  namespace = "jupiter-wheels",
)
public data class EndRentalEarlyDueToProblem(
  public val problemDescription: String,
  @TargetEntityId
  public val rentalId: String,
)
