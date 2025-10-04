package io.axoniq.build.jupiter_wheels.support_system.api

import kotlin.Boolean
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ApproveOrRejectPhoto",
  namespace = "jupiter-wheels",
)
public data class ApproveOrRejectPhoto(
  public val approved: Boolean,
  @TargetEntityId
  public val rentalId: String,
)
