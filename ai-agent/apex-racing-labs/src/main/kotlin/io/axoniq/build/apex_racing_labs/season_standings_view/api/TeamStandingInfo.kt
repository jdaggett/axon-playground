package io.axoniq.build.apex_racing_labs.season_standings_view.api

import kotlin.Double
import kotlin.Int
import kotlin.String

public data class TeamStandingInfo(
  public val totalRaces: Int,
  public val teamId: String,
  public val averageRating: Double?,
  public val teamName: String,
  public val position: Int,
)
