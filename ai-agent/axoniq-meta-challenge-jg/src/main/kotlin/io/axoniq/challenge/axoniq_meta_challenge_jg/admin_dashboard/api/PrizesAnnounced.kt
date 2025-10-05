package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api

import java.time.LocalDateTime
import org.axonframework.eventhandling.annotations.Event

@Event(
  name = "PrizesAnnounced",
  namespace = "axoniq-meta-challenge-jg",
)
public data class PrizesAnnounced(
  public val announcementTime: LocalDateTime,
)
