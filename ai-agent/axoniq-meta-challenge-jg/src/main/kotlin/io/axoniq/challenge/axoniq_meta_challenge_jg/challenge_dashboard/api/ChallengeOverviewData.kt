package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard.api

import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class ChallengeOverviewData(
  public val challengeTitle: String,
  public val requirements: List<String>,
  public val estimatedCompletionTime: Int,
)
