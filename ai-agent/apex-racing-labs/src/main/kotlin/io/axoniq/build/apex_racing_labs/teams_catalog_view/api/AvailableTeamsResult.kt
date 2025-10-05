package io.axoniq.build.apex_racing_labs.teams_catalog_view.api

import kotlin.collections.List

public data class AvailableTeamsResult(
  public val teams: List<TeamInfo>,
)
