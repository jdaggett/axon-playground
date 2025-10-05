package io.axoniq.build.apex_racing_labs.race_profile_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "RaceProfile",
  namespace = "apex-racing-labs",
)
public data class RaceProfile(
  public val raceId: String,
)
