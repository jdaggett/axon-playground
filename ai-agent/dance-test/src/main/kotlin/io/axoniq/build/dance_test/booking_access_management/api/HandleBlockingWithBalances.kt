package io.axoniq.build.dance_test.booking_access_management.api

import kotlin.Boolean
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "HandleBlockingWithBalances",
  namespace = "dance-test",
)
public data class HandleBlockingWithBalances(
  public val instructorId: String,
  public val preserveBalances: Boolean,
  @TargetEntityId
  public val studentId: String,
)
