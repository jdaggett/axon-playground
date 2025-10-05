package io.axoniq.build.apex_racing_labs.race_list_view.api

import java.time.LocalDate
import kotlin.String

public data class RaceSearchItem(
  public val raceId: String,
  public val raceDate: LocalDate,
  public val trackName: String,
)
