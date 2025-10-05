package io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "CreateAccount",
  namespace = "axoniq-meta-challenge-jg",
)
public data class CreateAccount(
  public val password: String,
  public val firstName: String,
  public val email: String,
  public val lastName: String,
)
