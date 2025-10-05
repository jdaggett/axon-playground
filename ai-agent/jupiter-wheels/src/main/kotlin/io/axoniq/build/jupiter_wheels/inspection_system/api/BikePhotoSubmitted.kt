package io.axoniq.build.jupiter_wheels.inspection_system.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BikePhotoSubmitted",
  namespace = "jupiter-wheels",
)
public data class BikePhotoSubmitted(
  public val photoUrl: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
