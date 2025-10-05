package io.axoniq.build.apex_racing_labs.team_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "TeamRemoved",
  namespace = "apex-racing-labs",
)
public data class TeamRemoved(
  @EventTag(key = "Team")
  public val teamId: String,
)
