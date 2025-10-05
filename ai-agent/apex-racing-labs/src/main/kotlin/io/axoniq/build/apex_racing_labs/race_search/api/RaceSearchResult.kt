package io.axoniq.build.apex_racing_labs.race_search.api

import kotlin.Boolean
import kotlin.collections.List

public data class RaceSearchResult(
  public val success: Boolean,
  public val races: List<RaceSearchItem>,
)
