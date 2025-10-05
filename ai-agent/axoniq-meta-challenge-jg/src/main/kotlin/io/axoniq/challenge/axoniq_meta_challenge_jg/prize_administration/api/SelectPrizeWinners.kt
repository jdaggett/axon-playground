package io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration.api

import kotlin.String
import kotlin.collections.List
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "SelectPrizeWinners",
  namespace = "axoniq-meta-challenge-jg",
)
public data class SelectPrizeWinners(
  public val employeeId: String,
  public val selectedWinnerIds: List<String>,
)
