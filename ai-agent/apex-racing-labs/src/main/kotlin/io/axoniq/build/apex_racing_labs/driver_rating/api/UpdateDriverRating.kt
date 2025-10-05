package io.axoniq.build.apex_racing_labs.driver_rating.api

import kotlin.Int
import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "UpdateDriverRating",
  namespace = "apex-racing-labs",
)
public data class UpdateDriverRating(
  public val userId: String,
  public val driverId: String,
  public val newRating: Int,
  public val raceId: String,
)
