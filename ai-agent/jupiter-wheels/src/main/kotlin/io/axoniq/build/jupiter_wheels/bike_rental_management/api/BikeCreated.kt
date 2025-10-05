package io.axoniq.build.jupiter_wheels.bike_rental_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BikeCreated",
  namespace = "jupiter-wheels",
)
public data class BikeCreated(
  public val location: String,
  public val bikeType: String,
  public val condition: String,
  @EventTag(key = "Bike")
  public val bikeId: String,
)
