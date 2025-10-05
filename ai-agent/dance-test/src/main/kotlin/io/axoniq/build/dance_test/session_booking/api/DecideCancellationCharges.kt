package io.axoniq.build.dance_test.session_booking.api

import kotlin.Boolean
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "DecideCancellationCharges",
  namespace = "dance-test",
)
public data class DecideCancellationCharges(
  public val chargeStudent: Boolean,
  public val reason: String,
  @TargetEntityId
  public val sessionId: String,
)
