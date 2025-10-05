package io.axoniq.build.apex_racing_labs.race_profile_view.api

import java.time.LocalDate
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class RaceProfileResult(
  public val totalRatings: Int,
  public val userComments: List<UserComment>,
  public val raceId: String,
  public val averageRating: Double?,
  public val participatingDrivers: List<DriverInfo>,
  public val status: String,
  public val raceDate: LocalDate,
  public val trackName: String,
)
