package io.axoniq.build.apex_racing_labs.race_profile_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "RaceCancelled",
  namespace = "apex-racing-labs",
)
public data class RaceCancelled(
  @EventTag(key = "Race")
  public val raceId: String,
)
