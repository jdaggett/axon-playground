package io.axoniq.build.apex_racing_labs.user_registration.api

import kotlin.Boolean
import kotlin.String

public data class EmailVerificationResult(
  public val success: Boolean,
  public val message: String,
)
