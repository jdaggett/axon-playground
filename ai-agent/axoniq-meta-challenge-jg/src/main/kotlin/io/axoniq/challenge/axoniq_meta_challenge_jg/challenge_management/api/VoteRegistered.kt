package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "VoteRegistered",
  namespace = "axoniq-meta-challenge-jg",
)
public data class VoteRegistered(
  @EventTag(key = "Participant")
  public val participantId: String,
  public val voteType: String,
  public val projectId: String,
)
