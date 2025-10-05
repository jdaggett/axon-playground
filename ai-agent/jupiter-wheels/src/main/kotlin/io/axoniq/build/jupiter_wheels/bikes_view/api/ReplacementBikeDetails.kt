package io.axoniq.build.jupiter_wheels.bikes_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "ReplacementBikeDetails",
  namespace = "jupiter-wheels",
)
public data class ReplacementBikeDetails(
  public val replacementBikeId: String,
)
