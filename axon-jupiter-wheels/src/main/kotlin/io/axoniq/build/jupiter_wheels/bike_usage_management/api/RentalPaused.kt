package io.axoniq.build.jupiter_wheels.bike_usage_management.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "RentalPaused",
  namespace = "jupiter-wheels",
)
public data class RentalPaused(
  public val pauseStartTime: LocalDateTime,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
