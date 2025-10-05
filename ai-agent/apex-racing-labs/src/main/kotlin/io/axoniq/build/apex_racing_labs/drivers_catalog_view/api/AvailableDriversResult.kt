package io.axoniq.build.apex_racing_labs.drivers_catalog_view.api

import kotlin.collections.List

public data class AvailableDriversResult(
  public val drivers: List<DriverInfo>,
)
