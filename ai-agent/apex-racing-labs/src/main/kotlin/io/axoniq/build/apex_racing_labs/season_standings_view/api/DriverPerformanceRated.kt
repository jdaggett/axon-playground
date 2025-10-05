package io.axoniq.build.apex_racing_labs.season_standings_view.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event

@Event(
  name = "DriverPerformanceRated",
  namespace = "apex-racing-labs",
)
public data class DriverPerformanceRated(
  public val userId: String,
  public val driverId: String,
  public val raceId: String,
  public val rating: Int,
)
