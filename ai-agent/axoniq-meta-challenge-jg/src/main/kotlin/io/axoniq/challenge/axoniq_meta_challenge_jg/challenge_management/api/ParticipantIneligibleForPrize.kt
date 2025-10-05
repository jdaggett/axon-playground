package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "ParticipantIneligibleForPrize",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ParticipantIneligibleForPrize(
  @EventTag(key = "Participant")
  public val participantId: String,
)
