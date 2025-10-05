package io.axoniq.build.apex_racing_labs.drivers_catalog_view.api

import kotlin.String

public data class DriverInfo(
  public val teamId: String,
  public val driverId: String,
  public val teamName: String,
  public val driverName: String,
)
