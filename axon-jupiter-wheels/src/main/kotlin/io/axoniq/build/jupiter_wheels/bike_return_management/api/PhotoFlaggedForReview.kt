package io.axoniq.build.jupiter_wheels.bike_return_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PhotoFlaggedForReview",
  namespace = "jupiter-wheels",
)
public data class PhotoFlaggedForReview(
  public val photoUrl: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
