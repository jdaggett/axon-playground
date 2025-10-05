package io.axoniq.build.jupiter_wheels.bike_rental_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "RentalRequestRejectedCancellation",
  namespace = "jupiter-wheels",
)
public data class RentalRequestRejectedCancellation(
  @EventTag(key = "Rental")
  public val rentalId: String,
  public val bikeId: String,
)
