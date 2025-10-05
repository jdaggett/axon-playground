package io.axoniq.build.apex_racing_labs.teams_catalog_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "TeamDetails",
  namespace = "apex-racing-labs",
)
public data class TeamDetails(
  public val teamId: String,
)
