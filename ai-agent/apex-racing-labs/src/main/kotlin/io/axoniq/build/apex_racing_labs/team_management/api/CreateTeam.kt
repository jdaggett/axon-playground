package io.axoniq.build.apex_racing_labs.team_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateTeam",
  namespace = "apex-racing-labs",
)
public data class CreateTeam(
  @TargetEntityId
  public val teamId: String,
  public val teamName: String,
)
