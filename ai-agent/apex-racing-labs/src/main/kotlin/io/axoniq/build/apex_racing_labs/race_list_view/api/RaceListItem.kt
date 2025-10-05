package io.axoniq.build.apex_racing_labs.race_list_view.api

import java.time.LocalDate
import kotlin.Double
import kotlin.Int
import kotlin.String

public data class RaceListItem(
  public val totalRatings: Int,
  public val raceId: String,
  public val averageRating: Double?,
  public val status: String,
  public val raceDate: LocalDate,
  public val trackName: String,
)
