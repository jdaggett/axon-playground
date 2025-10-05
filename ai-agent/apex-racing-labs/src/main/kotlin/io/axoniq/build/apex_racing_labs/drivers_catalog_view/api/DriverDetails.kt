package io.axoniq.build.apex_racing_labs.drivers_catalog_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DriverDetails",
  namespace = "apex-racing-labs",
)
public data class DriverDetails(
  public val driverId: String,
)
