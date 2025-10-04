package io.axoniq.build.jupiter_wheels.user_account_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "RegisterAccount",
  namespace = "jupiter-wheels",
)
public data class RegisterAccount(
  public val email: String,
  public val phoneNumber: String,
  public val name: String,
)
