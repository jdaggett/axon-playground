package io.axoniq.build.apex_racing_labs.driver_management.api

import kotlin.Boolean
import kotlin.String

public data class DriverRemovalResult(
  public val success: Boolean,
  public val message: String,
)
