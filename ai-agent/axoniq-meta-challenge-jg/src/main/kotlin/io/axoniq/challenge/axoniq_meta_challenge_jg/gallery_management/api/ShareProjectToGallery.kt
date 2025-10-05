package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ShareProjectToGallery",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ShareProjectToGallery(
  public val applicationId: String,
  @TargetEntityId
  public val participantId: String,
  public val projectTitle: String,
)
