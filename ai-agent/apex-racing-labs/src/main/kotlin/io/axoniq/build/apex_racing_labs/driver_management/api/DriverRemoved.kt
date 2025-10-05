package io.axoniq.build.apex_racing_labs.driver_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "DriverRemoved",
  namespace = "apex-racing-labs",
)
public data class DriverRemoved(
  @EventTag(key = "Driver")
  public val driverId: String,
)
