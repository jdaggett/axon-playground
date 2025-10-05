package io.axoniq.build.apex_racing_labs.user_statistics_view.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "RaceRated",
  namespace = "apex-racing-labs",
)
public data class RaceRated(
  @EventTag(key = "Race")
  public val raceId: String,
  public val userId: String,
  public val comment: String?,
  public val rating: Int,
)
