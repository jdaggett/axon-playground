package io.axoniq.build.jupiter_wheels.bike_usage_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "RentalEndedEarly",
  namespace = "jupiter-wheels",
)
public data class RentalEndedEarly(
  public val problemDescription: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
