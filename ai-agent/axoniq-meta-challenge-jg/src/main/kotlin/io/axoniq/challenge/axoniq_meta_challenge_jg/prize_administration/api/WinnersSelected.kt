package io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration.api

import java.time.LocalDateTime
import kotlin.String
import kotlin.collections.List
import org.axonframework.eventhandling.annotations.Event

@Event(
  name = "WinnersSelected",
  namespace = "axoniq-meta-challenge-jg",
)
public data class WinnersSelected(
  public val selectionTime: LocalDateTime,
  public val winnerIds: List<String>,
)
