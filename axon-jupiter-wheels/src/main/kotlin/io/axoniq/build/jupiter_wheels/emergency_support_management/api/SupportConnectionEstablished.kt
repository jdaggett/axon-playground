package io.axoniq.build.jupiter_wheels.emergency_support_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "SupportConnectionEstablished",
  namespace = "jupiter-wheels",
)
public data class SupportConnectionEstablished(
  public val supportAgentId: String,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
