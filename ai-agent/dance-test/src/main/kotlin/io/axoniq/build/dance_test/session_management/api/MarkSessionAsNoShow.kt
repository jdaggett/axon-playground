package io.axoniq.build.dance_test.session_management.api

import kotlin.Boolean
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "MarkSessionAsNoShow",
  namespace = "dance-test",
)
public data class MarkSessionAsNoShow(
  public val chargeStudent: Boolean,
  public val reason: String,
  @TargetEntityId
  public val sessionId: String,
)
