package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api

import java.time.LocalDateTime
import kotlin.String

public data class ParticipantResult(
  public val participantEmail: String,
  public val participantId: String,
  public val completionTime: LocalDateTime,
)
