package io.axoniq.build.apex_racing_labs.race_profile_view.api

import kotlin.Double
import kotlin.String

public data class DriverInfo(
  public val driverId: String,
  public val averageRating: Double?,
  public val driverName: String,
)
