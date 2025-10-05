package io.axoniq.build.apex_racing_labs.race_search.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event

@Event(
  name = "RaceSearchPerformed",
  namespace = "apex-racing-labs",
)
public data class RaceSearchPerformed(
  public val searchTerm: String,
  public val resultsCount: Int,
  public val userId: String?,
)
