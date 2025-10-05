package io.axoniq.build.dance_test.session_management.api

import kotlin.Int
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CompleteSessionWithReducedTime",
  namespace = "dance-test",
)
public data class CompleteSessionWithReducedTime(
  public val actualDuration: Int,
  public val notes: String?,
  @TargetEntityId
  public val sessionId: String,
)
