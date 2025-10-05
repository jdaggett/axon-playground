package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "ChallengeStarted",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ChallengeStarted(
  @EventTag(key = "Participant")
  public val participantId: String,
)
