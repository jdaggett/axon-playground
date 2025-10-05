package io.axoniq.build.apex_racing_labs.teams_catalog_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "TeamCreated",
  namespace = "apex-racing-labs",
)
public data class TeamCreated(
  @EventTag(key = "Team")
  public val teamId: String,
  public val teamName: String,
)
