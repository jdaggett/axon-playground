package io.axoniq.build.apex_racing_labs.driver_rating.api

import kotlin.Int
import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "RateDriverPerformance",
  namespace = "apex-racing-labs",
)
public data class RateDriverPerformance(
  public val userId: String,
  public val driverId: String,
  public val raceId: String,
  public val rating: Int,
)
