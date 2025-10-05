package io.axoniq.build.dance_test.communication_management.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PaymentReminderSent",
  namespace = "dance-test",
)
public data class PaymentReminderSent(
  public val instructorId: String,
  public val sentDate: LocalDateTime,
  @EventTag(key = "Student")
  public val studentId: String,
  public val reminderType: String,
)
