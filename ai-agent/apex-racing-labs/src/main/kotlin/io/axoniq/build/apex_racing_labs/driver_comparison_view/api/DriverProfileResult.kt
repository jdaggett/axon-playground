package io.axoniq.build.apex_racing_labs.driver_comparison_view.api

import kotlin.Double
import kotlin.String

public data class DriverProfileResult(
  public val teamId: String,
  public val driverId: String,
  public val overallRating: Double?,
  public val teamName: String,
  public val driverName: String,
)
