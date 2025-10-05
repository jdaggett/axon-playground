package io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "RequestPasswordReset",
  namespace = "axoniq-meta-challenge-jg",
)
public data class RequestPasswordReset(
  public val email: String,
)
