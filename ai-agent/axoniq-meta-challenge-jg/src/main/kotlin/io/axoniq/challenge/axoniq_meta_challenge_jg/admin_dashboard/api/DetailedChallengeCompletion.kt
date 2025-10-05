package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DetailedChallengeCompletion",
  namespace = "axoniq-meta-challenge-jg",
)
public data class DetailedChallengeCompletion(
  public val participantId: String,
)
