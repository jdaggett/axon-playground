package io.axoniq.build.apex_racing_labs.driver_history_view.api

import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class DriverHistoryResult(
  public val totalRatings: Int,
  public val bestRaces: List<DriverRaceHistory>,
  public val overallAverageRating: Double?,
  public val driverId: String,
  public val driverName: String,
)
