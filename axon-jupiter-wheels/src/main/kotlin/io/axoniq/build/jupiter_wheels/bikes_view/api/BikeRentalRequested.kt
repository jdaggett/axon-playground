package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BikeRentalRequested",
  namespace = "jupiter-wheels",
)
public data class BikeRentalRequested(
  public val userId: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
  public val bikeId: String,
)
