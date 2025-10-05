package io.axoniq.build.apex_racing_labs.driver_performance_view.api

import kotlin.Double
import kotlin.Int
import kotlin.String

public data class DriverRacePerformanceResult(
  public val totalRatings: Int,
  public val communityRating: Double?,
  public val driverId: String,
  public val personalRating: Int?,
  public val raceId: String,
  public val averageRating: Double?,
  public val driverName: String,
)
