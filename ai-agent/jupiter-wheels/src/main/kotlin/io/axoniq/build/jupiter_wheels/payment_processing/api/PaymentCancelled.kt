package io.axoniq.build.jupiter_wheels.payment_processing.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PaymentCancelled",
  namespace = "jupiter-wheels",
)
public data class PaymentCancelled(
  public val paymentId: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
