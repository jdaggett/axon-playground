package io.axoniq.build.apex_racing_labs.user_registration.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "CreateAccount",
  namespace = "apex-racing-labs",
)
public data class CreateAccount(
  public val password: String,
  public val email: String,
)
