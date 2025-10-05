package io.axoniq.build.apex_racing_labs.race_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CancelRace",
  namespace = "apex-racing-labs",
)
public data class CancelRace(
  @TargetEntityId
  public val raceId: String,
)
