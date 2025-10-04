package io.axoniq.build.jupiter_wheels.payment_processing.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RetryPayment",
  namespace = "jupiter-wheels",
)
public data class RetryPayment(
  public val paymentMethod: String,
  @TargetEntityId
  public val rentalId: String,
)
