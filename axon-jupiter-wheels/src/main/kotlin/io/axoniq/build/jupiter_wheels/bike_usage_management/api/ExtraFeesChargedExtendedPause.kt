package io.axoniq.build.jupiter_wheels.bike_usage_management.api

import kotlin.Double
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "ExtraFeesChargedExtendedPause",
  namespace = "jupiter-wheels",
)
public data class ExtraFeesChargedExtendedPause(
  public val extraFee: Double,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
