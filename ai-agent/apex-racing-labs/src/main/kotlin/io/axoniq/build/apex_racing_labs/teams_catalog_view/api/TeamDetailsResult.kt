package io.axoniq.build.apex_racing_labs.teams_catalog_view.api

import kotlin.Boolean
import kotlin.String

public data class TeamDetailsResult(
  public val teamId: String,
  public val active: Boolean,
  public val teamName: String,
)
