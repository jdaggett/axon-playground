package io.axoniq.build.jupiter_wheels.user_account_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "VerifyEmail",
  namespace = "jupiter-wheels",
)
public data class VerifyEmail(
  @TargetEntityId
  public val userId: String,
  public val verificationToken: String,
)
