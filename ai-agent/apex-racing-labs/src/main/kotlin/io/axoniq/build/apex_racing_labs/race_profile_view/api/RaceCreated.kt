package io.axoniq.build.apex_racing_labs.race_profile_view.api

import java.time.LocalDate
import kotlin.String
import kotlin.collections.List
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "RaceCreated",
  namespace = "apex-racing-labs",
)
public data class RaceCreated(
  public val participatingDriverIds: List<String>,
  @EventTag(key = "Race")
  public val raceId: String,
  public val raceDate: LocalDate,
  public val trackName: String,
)
