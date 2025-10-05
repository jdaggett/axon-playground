package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "VoteForProject",
  namespace = "axoniq-meta-challenge-jg",
)
public data class VoteForProject(
  @TargetEntityId
  public val participantId: String,
  public val voteType: String,
  public val projectId: String,
)
