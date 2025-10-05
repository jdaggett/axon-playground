package io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "ParticipantAuthenticated",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ParticipantAuthenticated(
  @EventTag(key = "Participant")
  public val participantId: String,
  public val email: String,
  public val authenticationMethod: String,
)
