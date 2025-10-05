package io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PasswordResetEmailSent",
  namespace = "axoniq-meta-challenge-jg",
)
public data class PasswordResetEmailSent(
  @EventTag(key = "Participant")
  public val participantId: String,
  public val email: String,
)
