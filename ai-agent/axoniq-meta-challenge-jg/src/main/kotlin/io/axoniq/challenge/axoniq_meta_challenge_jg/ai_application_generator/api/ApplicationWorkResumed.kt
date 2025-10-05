package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "ApplicationWorkResumed",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ApplicationWorkResumed(
  public val applicationId: String,
  @EventTag(key = "Participant")
  public val participantId: String,
)
