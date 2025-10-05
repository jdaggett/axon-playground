package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "ProjectSharedToGallery",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ProjectSharedToGallery(
  public val submissionTime: LocalDateTime,
  @EventTag(key = "Participant")
  public val participantId: String,
  public val projectTitle: String,
  public val projectId: String,
)
