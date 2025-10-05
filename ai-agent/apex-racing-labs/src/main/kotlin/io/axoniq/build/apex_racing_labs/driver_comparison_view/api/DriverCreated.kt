package io.axoniq.build.apex_racing_labs.driver_comparison_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "DriverCreated",
  namespace = "apex-racing-labs",
)
public data class DriverCreated(
  public val teamId: String,
  @EventTag(key = "Driver")
  public val driverId: String,
  public val driverName: String,
)
