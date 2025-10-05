package io.axoniq.build.jupiter_wheels.drop_off_zones_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "AvailableDropOffZones",
  namespace = "jupiter-wheels",
)
public data class AvailableDropOffZones(
  public val userLocation: String?,
)
