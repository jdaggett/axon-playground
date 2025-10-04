package io.axoniq.build.jupiter_wheels.rental_extension_management.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "GracePeriodActivated",
  namespace = "jupiter-wheels",
)
public data class GracePeriodActivated(
  public val gracePeriodMinutes: Int,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
