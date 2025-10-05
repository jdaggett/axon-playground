package io.axoniq.build.apex_racing_labs.race_rating.api

import kotlin.Int
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RateRace",
  namespace = "apex-racing-labs",
)
public data class RateRace(
  @TargetEntityId
  public val raceId: String,
  public val userId: String,
  public val comment: String?,
  public val rating: Int,
)
