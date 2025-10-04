package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "BikeMaintenanceDetails",
  namespace = "jupiter-wheels",
)
public data class BikeMaintenanceDetails(
  public val bikeId: String,
)
