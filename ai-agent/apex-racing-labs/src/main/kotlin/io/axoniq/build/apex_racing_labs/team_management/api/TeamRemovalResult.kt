package io.axoniq.build.apex_racing_labs.team_management.api

import kotlin.Boolean
import kotlin.String

public data class TeamRemovalResult(
  public val success: Boolean,
  public val message: String,
)
