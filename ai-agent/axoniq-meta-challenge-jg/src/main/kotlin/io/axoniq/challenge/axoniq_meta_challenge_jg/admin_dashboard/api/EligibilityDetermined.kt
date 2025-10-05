package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api

import kotlin.Boolean
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "EligibilityDetermined",
  namespace = "axoniq-meta-challenge-jg",
)
public data class EligibilityDetermined(
  @EventTag(key = "Participant")
  public val participantId: String,
  public val isEligible: Boolean,
)
