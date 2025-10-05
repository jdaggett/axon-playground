package io.axoniq.build.apex_racing_labs.race_profile_view.api

import kotlin.Int
import kotlin.String

public data class UserComment(
  public val userId: String,
  public val comment: String?,
  public val rating: Int,
)
