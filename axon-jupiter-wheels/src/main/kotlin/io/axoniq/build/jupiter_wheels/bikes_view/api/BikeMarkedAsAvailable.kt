package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BikeMarkedAsAvailable",
  namespace = "jupiter-wheels",
)
public data class BikeMarkedAsAvailable(
  @EventTag(key = "Bike")
  public val bikeId: String,
)
