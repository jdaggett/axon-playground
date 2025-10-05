package io.axoniq.build.apex_racing_labs.driver_history_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DriverPerformanceHistory",
  namespace = "apex-racing-labs",
)
public data class DriverPerformanceHistory(
  public val driverId: String,
)
