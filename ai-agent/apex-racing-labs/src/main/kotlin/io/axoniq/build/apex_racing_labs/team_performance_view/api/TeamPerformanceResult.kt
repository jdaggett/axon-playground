package io.axoniq.build.apex_racing_labs.team_performance_view.api

import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class TeamPerformanceResult(
  public val totalRaces: Int,
  public val bestRaces: List<TeamRaceInfo>,
  public val teamId: String,
  public val averageRating: Double?,
  public val teamName: String,
)
