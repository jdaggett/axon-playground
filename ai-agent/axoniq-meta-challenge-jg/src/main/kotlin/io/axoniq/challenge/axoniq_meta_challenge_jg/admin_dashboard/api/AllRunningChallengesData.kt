package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api

import kotlin.collections.List

public data class AllRunningChallengesData(
  public val runningChallenges: List<ChallengeStatus>,
)
