package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "ProgressDashboard",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ProgressDashboard(
  public val participantId: String,
)
