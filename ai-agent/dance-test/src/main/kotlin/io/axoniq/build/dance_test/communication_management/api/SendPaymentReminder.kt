package io.axoniq.build.dance_test.communication_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "SendPaymentReminder",
  namespace = "dance-test",
)
public data class SendPaymentReminder(
  public val instructorId: String,
  public val message: String,
  @TargetEntityId
  public val studentId: String,
  public val reminderType: String,
)
