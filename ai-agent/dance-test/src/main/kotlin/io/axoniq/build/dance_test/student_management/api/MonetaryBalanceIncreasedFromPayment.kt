package io.axoniq.build.dance_test.student_management.api

import kotlin.Double
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "MonetaryBalanceIncreasedFromPayment",
  namespace = "dance-test",
)
public data class MonetaryBalanceIncreasedFromPayment(
  public val amount: Double,
  @EventTag(key = "Student")
  public val studentId: String,
)
