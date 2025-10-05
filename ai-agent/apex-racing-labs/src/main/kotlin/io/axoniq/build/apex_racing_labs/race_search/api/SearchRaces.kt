package io.axoniq.build.apex_racing_labs.race_search.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "SearchRaces",
  namespace = "apex-racing-labs",
)
public data class SearchRaces(
  public val searchTerm: String,
  public val userId: String?,
)
