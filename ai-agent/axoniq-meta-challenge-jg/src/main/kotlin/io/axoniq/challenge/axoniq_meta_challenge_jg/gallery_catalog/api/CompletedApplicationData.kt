package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_catalog.api

import kotlin.Boolean
import kotlin.String

public data class CompletedApplicationData(
  public val applicationTitle: String,
  public val applicationId: String,
  public val isReadyForSharing: Boolean,
)
