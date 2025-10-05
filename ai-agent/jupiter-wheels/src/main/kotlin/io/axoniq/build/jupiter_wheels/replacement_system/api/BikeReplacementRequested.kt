package io.axoniq.build.jupiter_wheels.replacement_system.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BikeReplacementRequested",
  namespace = "jupiter-wheels",
)
public data class BikeReplacementRequested(
  @EventTag(key = "Bike")
  public val originalBikeId: String,
  public val rentalId: String,
  public val issueDescription: String,
)
