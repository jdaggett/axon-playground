package io.axoniq.build.jupiter_wheels.bike_usage_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BikeMarkedAsInUse",
  namespace = "jupiter-wheels",
)
public data class BikeMarkedAsInUse(
  public val rentalId: String,
  @EventTag(key = "Bike")
  public val bikeId: String,
)
