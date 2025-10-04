package io.axoniq.build.jupiter_wheels.bike_usage_management.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "RentalResumed",
  namespace = "jupiter-wheels",
)
public data class RentalResumed(
  public val pauseDuration: Int,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
