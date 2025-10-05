package io.axoniq.build.apex_racing_labs.driver_comparison_view.api

import kotlin.String
import kotlin.collections.List

public data class DriverComparisonResult(
  public val driverId: String,
  public val rivalDriverName: String,
  public val rivalDriverId: String,
  public val headToHeadRaces: List<RaceComparison>,
  public val driverName: String,
)
