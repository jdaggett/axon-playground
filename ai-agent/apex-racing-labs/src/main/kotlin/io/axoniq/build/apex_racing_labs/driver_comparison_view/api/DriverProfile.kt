package io.axoniq.build.apex_racing_labs.driver_comparison_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DriverProfile",
  namespace = "apex-racing-labs",
)
public data class DriverProfile(
  public val driverId: String,
)
