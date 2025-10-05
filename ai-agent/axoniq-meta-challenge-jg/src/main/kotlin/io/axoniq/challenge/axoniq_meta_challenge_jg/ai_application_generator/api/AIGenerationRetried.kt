package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.api

import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "AIGenerationRetried",
  namespace = "axoniq-meta-challenge-jg",
)
public data class AIGenerationRetried(
  public val retryAttempt: Int,
  @EventTag(key = "Participant")
  public val participantId: String,
)
