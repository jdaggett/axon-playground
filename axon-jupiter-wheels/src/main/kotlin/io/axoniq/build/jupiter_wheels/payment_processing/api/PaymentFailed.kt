package io.axoniq.build.jupiter_wheels.payment_processing.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PaymentFailed",
  namespace = "jupiter-wheels",
)
public data class PaymentFailed(
  public val failureReason: String,
  public val paymentId: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
