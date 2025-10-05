package io.axoniq.build.apex_racing_labs.season_standings_view.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event

@Event(
  name = "DriverRatingUpdated",
  namespace = "apex-racing-labs",
)
public data class DriverRatingUpdated(
  public val userId: String,
  public val driverId: String,
  public val newRating: Int,
  public val raceId: String,
  public val previousRating: Int,
)
