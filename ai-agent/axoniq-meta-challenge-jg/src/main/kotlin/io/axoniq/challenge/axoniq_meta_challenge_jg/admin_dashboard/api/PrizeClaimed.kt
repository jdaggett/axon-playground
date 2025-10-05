package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PrizeClaimed",
  namespace = "axoniq-meta-challenge-jg",
)
public data class PrizeClaimed(
  @EventTag(key = "Participant")
  public val participantId: String,
  public val prizeId: String,
  public val claimTime: LocalDateTime,
)
