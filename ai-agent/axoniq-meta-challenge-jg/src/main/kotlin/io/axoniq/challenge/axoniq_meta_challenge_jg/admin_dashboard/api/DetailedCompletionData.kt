package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api

import java.time.LocalDateTime
import kotlin.Boolean
import kotlin.String

public data class DetailedCompletionData(
  public val participantEmail: String,
  public val voteCast: Boolean,
  public val participantId: String,
  public val applicationCreated: Boolean,
  public val projectShared: Boolean,
  public val completionTime: LocalDateTime,
)
