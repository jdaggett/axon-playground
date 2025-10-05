package io.axoniq.build.apex_racing_labs.user_registration.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "VerifyEmail",
  namespace = "apex-racing-labs",
)
public data class VerifyEmail(
  public val verificationToken: String,
)
