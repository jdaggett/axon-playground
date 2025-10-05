package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard.api

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class ProgressDashboardData(
  public val completionPercentage: Int,
  public val voteCast: Boolean,
  public val participantId: String,
  public val stepInstructions: List<String>,
  public val applicationCreated: Boolean,
  public val isEligible: Boolean,
  public val projectShared: Boolean,
)
