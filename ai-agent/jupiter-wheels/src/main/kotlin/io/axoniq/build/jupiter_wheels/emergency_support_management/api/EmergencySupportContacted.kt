package io.axoniq.build.jupiter_wheels.emergency_support_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "EmergencySupportContacted",
  namespace = "jupiter-wheels",
)
public data class EmergencySupportContacted(
  public val emergencyType: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
