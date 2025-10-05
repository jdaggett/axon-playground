package io.axoniq.build.apex_racing_labs.drivers_catalog_view.api

import kotlin.Boolean
import kotlin.String

public data class DriverDetailsResult(
  public val teamId: String,
  public val driverId: String,
  public val active: Boolean,
  public val teamName: String,
  public val driverName: String,
)
