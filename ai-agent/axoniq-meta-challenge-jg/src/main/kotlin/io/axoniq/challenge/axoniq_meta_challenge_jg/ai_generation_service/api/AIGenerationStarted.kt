package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_generation_service.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "AIGenerationStarted",
  namespace = "axoniq-meta-challenge-jg",
)
public data class AIGenerationStarted(
  @EventTag(key = "Participant")
  public val participantId: String,
  public val applicationParameters: String,
)
