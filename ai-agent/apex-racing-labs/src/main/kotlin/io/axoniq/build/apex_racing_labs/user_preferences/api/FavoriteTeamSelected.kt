package io.axoniq.build.apex_racing_labs.user_preferences.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "FavoriteTeamSelected",
  namespace = "apex-racing-labs",
)
public data class FavoriteTeamSelected(
  public val teamId: String,
  @EventTag(key = "User")
  public val userId: String,
)
