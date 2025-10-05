package io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ClaimPrize",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ClaimPrize(
  @TargetEntityId
  public val participantId: String,
  public val prizeId: String,
)
