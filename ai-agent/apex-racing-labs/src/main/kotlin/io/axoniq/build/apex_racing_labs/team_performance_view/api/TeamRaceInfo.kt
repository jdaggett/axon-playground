package io.axoniq.build.apex_racing_labs.team_performance_view.api

import java.time.LocalDate
import kotlin.Double
import kotlin.String

public data class TeamRaceInfo(
  public val raceId: String,
  public val raceDate: LocalDate,
  public val trackName: String,
  public val averageRating: Double,
)
