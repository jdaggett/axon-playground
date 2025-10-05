package io.axoniq.build.apex_racing_labs.race_list_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "RaceSearchResults",
  namespace = "apex-racing-labs",
)
public data class RaceSearchResults(
  public val searchTerm: String,
)
