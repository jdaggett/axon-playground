package io.axoniq.build.jupiter_wheels.rental_extension_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PaymentCompleted",
  namespace = "jupiter-wheels",
)
public data class PaymentCompleted(
  public val paymentId: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
