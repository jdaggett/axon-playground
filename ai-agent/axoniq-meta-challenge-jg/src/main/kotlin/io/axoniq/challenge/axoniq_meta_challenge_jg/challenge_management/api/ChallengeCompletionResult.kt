package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.api

import kotlin.Boolean
import kotlin.Int

public data class ChallengeCompletionResult(
  public val completionPercentage: Int,
  public val isEligible: Boolean,
)
