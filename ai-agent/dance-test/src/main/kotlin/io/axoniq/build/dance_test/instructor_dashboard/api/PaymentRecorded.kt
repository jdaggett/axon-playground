package io.axoniq.build.dance_test.instructor_dashboard.api

import java.time.LocalDate
import kotlin.Double
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PaymentRecorded",
  namespace = "dance-test",
)
public data class PaymentRecorded(
  public val amount: Double,
  public val paymentMethod: String,
  public val paymentDate: LocalDate,
  @EventTag(key = "Student")
  public val studentId: String,
)
