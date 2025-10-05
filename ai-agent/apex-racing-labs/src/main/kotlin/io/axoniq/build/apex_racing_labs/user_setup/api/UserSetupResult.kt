package io.axoniq.build.apex_racing_labs.user_setup.api

import kotlin.Boolean
import kotlin.String

public data class UserSetupResult(
  public val success: Boolean,
  public val message: String,
)
