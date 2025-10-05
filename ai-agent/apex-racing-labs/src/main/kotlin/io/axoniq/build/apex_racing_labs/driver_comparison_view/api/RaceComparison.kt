package io.axoniq.build.apex_racing_labs.driver_comparison_view.api

import java.time.LocalDate
import kotlin.Double
import kotlin.String

public data class RaceComparison(
  public val rivalRating: Double?,
  public val driverRating: Double?,
  public val raceId: String,
  public val raceDate: LocalDate,
  public val trackName: String,
)
