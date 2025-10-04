package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "BikeDetails",
  namespace = "jupiter-wheels",
)
public data class BikeDetails(
  public val bikeId: String,
)
