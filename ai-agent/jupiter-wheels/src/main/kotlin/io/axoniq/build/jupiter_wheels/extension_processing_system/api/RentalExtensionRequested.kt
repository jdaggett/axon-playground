package io.axoniq.build.jupiter_wheels.extension_processing_system.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "RentalExtensionRequested",
  namespace = "jupiter-wheels",
)
public data class RentalExtensionRequested(
  public val additionalTime: Int,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
