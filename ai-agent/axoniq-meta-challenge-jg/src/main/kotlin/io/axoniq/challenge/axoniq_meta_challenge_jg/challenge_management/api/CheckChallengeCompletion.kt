package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CheckChallengeCompletion",
  namespace = "axoniq-meta-challenge-jg",
)
public data class CheckChallengeCompletion(
  @TargetEntityId
  public val participantId: String,
)
