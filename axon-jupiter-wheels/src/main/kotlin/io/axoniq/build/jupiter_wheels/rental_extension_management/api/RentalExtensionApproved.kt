package io.axoniq.build.jupiter_wheels.rental_extension_management.api

import java.time.LocalDateTime
import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "RentalExtensionApproved",
  namespace = "jupiter-wheels",
)
public data class RentalExtensionApproved(
  public val approvedTime: Int,
  public val newEndTime: LocalDateTime,
  @EventTag(key = "Rental")
  public val rentalId: String,
)
