package io.axoniq.build.apex_racing_labs.race_management.api

import kotlin.Boolean
import kotlin.String

public data class RaceCancellationResult(
  public val success: Boolean,
  public val message: String,
)
