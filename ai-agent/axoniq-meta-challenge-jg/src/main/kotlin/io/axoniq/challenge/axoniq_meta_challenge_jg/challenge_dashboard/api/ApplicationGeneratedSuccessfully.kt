package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "ApplicationGeneratedSuccessfully",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ApplicationGeneratedSuccessfully(
  public val applicationId: String,
  @EventTag(key = "Participant")
  public val participantId: String,
)
