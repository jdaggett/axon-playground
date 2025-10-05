package io.axoniq.build.jupiter_wheels.payment_gateway.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ReportPaymentFailure",
  namespace = "jupiter-wheels",
)
public data class ReportPaymentFailure(
  public val failureReason: String,
  public val paymentId: String,
  @TargetEntityId
  public val rentalId: String,
)
