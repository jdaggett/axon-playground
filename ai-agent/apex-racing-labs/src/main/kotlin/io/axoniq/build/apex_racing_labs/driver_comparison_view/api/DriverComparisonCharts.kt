package io.axoniq.build.apex_racing_labs.driver_comparison_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DriverComparisonCharts",
  namespace = "apex-racing-labs",
)
public data class DriverComparisonCharts(
  public val driverId: String,
  public val rivalDriverId: String,
)
