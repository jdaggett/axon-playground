package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_catalog.api

import java.time.LocalDateTime
import kotlin.String

public data class ProjectSummary(
  public val submissionTime: LocalDateTime,
  public val creatorName: String,
  public val projectTitle: String,
  public val projectId: String,
)
