package io.axoniq.build.apex_racing_labs.season_standings_view.api

import kotlin.collections.List

public data class SeasonStandingsResult(
  public val standings: List<TeamStandingInfo>,
)
