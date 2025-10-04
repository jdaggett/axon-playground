package io.axoniq.build.jupiter_wheels.payment_processing.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ReturnPaymentDetails",
  namespace = "jupiter-wheels",
)
public data class ReturnPaymentDetails(
  public val paymentId: String,
  @TargetEntityId
  public val rentalId: String,
)
