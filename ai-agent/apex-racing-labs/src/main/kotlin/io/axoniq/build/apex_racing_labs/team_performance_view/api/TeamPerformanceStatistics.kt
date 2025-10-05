package io.axoniq.build.apex_racing_labs.team_performance_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "TeamPerformanceStatistics",
  namespace = "apex-racing-labs",
)
public data class TeamPerformanceStatistics(
  public val teamId: String,
)
