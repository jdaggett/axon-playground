package io.axoniq.build.apex_racing_labs.team_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RemoveTeam",
  namespace = "apex-racing-labs",
)
public data class RemoveTeam(
  @TargetEntityId
  public val teamId: String,
)
