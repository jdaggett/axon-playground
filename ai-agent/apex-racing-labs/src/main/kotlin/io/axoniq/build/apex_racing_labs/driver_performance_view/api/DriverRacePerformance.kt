package io.axoniq.build.apex_racing_labs.driver_performance_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DriverRacePerformance",
  namespace = "apex-racing-labs",
)
public data class DriverRacePerformance(
  public val driverId: String,
  public val raceId: String,
)
