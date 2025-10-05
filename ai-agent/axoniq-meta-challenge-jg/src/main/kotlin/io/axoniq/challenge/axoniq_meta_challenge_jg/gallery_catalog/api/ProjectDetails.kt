package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_catalog.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "ProjectDetails",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ProjectDetails(
  public val projectId: String,
)
