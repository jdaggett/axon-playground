package io.axoniq.build.jupiter_wheels.emergency_support_management.api

import kotlin.Double
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ProvideGPSLocation",
  namespace = "jupiter-wheels",
)
public data class ProvideGPSLocation(
  public val latitude: Double,
  public val longitude: Double,
  @TargetEntityId
  public val rentalId: String,
)
