package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "ReplacementBikeAssigned",
  namespace = "jupiter-wheels",
)
public data class ReplacementBikeAssigned(
  public val originalBikeId: String,
  public val replacementBikeId: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
