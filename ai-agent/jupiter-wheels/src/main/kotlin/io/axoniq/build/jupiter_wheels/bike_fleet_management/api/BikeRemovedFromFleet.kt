package io.axoniq.build.jupiter_wheels.bike_fleet_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BikeRemovedFromFleet",
  namespace = "jupiter-wheels",
)
public data class BikeRemovedFromFleet(
  public val removalReason: String,
  @EventTag(key = "Bike")
  public val bikeId: String,
)
