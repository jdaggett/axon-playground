package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "PrizeCollectionInstructions",
  namespace = "axoniq-meta-challenge-jg",
)
public data class PrizeCollectionInstructions(
  public val participantId: String,
)
