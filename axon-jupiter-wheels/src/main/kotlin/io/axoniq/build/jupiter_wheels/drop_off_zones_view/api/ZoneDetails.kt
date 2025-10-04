package io.axoniq.build.jupiter_wheels.drop_off_zones_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "ZoneDetails",
  namespace = "jupiter-wheels",
)
public data class ZoneDetails(
  public val zoneId: String,
)
