package io.axoniq.build.apex_racing_labs.user_statistics_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "PersonalVsCommunityStatistics",
  namespace = "apex-racing-labs",
)
public data class PersonalVsCommunityStatistics(
  public val userId: String,
)
