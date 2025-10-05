package io.axoniq.build.apex_racing_labs.user_setup.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CompleteInitialSetup",
  namespace = "apex-racing-labs",
)
public data class CompleteInitialSetup(
  @TargetEntityId
  public val userId: String,
  public val favoriteTeamId: String?,
  public val favoriteDriverId: String?,
)
