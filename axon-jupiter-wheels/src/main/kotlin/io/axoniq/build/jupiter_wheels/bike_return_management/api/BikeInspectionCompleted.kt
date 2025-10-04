package io.axoniq.build.jupiter_wheels.bike_return_management.api

import kotlin.Boolean
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BikeInspectionCompleted",
  namespace = "jupiter-wheels",
)
public data class BikeInspectionCompleted(
  public val inspectionPassed: Boolean,
  @EventTag(key = "Rental")
  public val rentalId: String,
  public val bikeId: String,
)
