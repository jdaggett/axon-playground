package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "SimilarNearbyBikes",
  namespace = "jupiter-wheels",
)
public data class SimilarNearbyBikes(
  public val location: String,
  public val bikeType: String?,
)
