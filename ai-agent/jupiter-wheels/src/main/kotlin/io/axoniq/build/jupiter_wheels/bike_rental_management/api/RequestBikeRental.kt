package io.axoniq.build.jupiter_wheels.bike_rental_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RequestBikeRental",
  namespace = "jupiter-wheels",
)
public data class RequestBikeRental(
  public val userId: String,
  public val bikeId: String,
) {
  @TargetEntityId
  public fun modelIdentifier(): TargetIdentifier = TargetIdentifier(userId, bikeId)

  public data class TargetIdentifier(
    public val userId: String,
    public val bikeId: String,
  )
}
