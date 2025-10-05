package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_catalog.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "CompletedApplication",
  namespace = "axoniq-meta-challenge-jg",
)
public data class CompletedApplication(
  public val participantId: String,
)
