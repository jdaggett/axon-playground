package io.axoniq.build.apex_racing_labs.user_preferences.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "SelectFavoriteDriver",
  namespace = "apex-racing-labs",
)
public data class SelectFavoriteDriver(
  @TargetEntityId
  public val userId: String,
  public val driverId: String,
)
