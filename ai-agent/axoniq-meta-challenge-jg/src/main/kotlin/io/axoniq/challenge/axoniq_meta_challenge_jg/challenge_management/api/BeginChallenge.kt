package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "BeginChallenge",
  namespace = "axoniq-meta-challenge-jg",
)
public data class BeginChallenge(
  @TargetEntityId
  public val participantId: String,
)
