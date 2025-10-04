package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "AvailableBikes",
  namespace = "jupiter-wheels",
)
public data class AvailableBikes(
  public val location: String?,
)
