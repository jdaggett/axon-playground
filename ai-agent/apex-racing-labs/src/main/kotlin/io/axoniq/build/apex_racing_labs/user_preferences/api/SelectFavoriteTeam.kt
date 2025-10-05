package io.axoniq.build.apex_racing_labs.user_preferences.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "SelectFavoriteTeam",
  namespace = "apex-racing-labs",
)
public data class SelectFavoriteTeam(
  public val teamId: String,
  @TargetEntityId
  public val userId: String,
)
