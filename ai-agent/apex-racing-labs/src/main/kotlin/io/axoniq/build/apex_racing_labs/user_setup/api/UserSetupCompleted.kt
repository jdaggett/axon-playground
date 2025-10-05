package io.axoniq.build.apex_racing_labs.user_setup.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "UserSetupCompleted",
  namespace = "apex-racing-labs",
)
public data class UserSetupCompleted(
  @EventTag(key = "User")
  public val userId: String,
  public val favoriteTeamId: String?,
  public val favoriteDriverId: String?,
)
