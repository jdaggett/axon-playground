package io.axoniq.build.jupiter_wheels.payment_gateway.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "RetryPayment",
  namespace = "jupiter-wheels",
)
public data class RetryPayment(
  public val paymentMethod: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
