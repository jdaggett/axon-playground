package io.axoniq.build.apex_racing_labs.user_statistics_view.api

import kotlin.Double
import kotlin.Int
import kotlin.String

public data class RatingComparison(
  public val raceId: String,
  public val personalRating: Int,
  public val communityRating: Double,
  public val difference: Double,
  public val trackName: String,
)
