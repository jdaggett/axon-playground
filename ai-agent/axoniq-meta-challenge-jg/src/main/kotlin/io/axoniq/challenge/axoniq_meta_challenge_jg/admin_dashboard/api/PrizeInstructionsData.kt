package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api

import kotlin.String

public data class PrizeInstructionsData(
  public val requiredIdentification: String,
  public val boothLocation: String,
  public val availableHours: String,
)
