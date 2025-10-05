package io.axoniq.build.apex_racing_labs.user_statistics_view.api

import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class UserStatisticsResult(
  public val ratingDifferences: List<RatingComparison>,
  public val userId: String,
  public val communityAverageRating: Double?,
  public val personalAverageRating: Double?,
  public val totalRatingsGiven: Int,
)
