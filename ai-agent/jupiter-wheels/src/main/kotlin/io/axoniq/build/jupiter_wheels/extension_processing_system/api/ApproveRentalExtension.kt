package io.axoniq.build.jupiter_wheels.extension_processing_system.api

import kotlin.Int
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ApproveRentalExtension",
  namespace = "jupiter-wheels",
)
public data class ApproveRentalExtension(
  public val approvedTime: Int,
  @TargetEntityId
  public val rentalId: String,
)
