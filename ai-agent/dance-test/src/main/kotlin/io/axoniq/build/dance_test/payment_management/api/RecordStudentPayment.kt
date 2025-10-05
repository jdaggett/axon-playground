package io.axoniq.build.dance_test.payment_management.api

import java.time.LocalDate
import kotlin.Double
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RecordStudentPayment",
  namespace = "dance-test",
)
public data class RecordStudentPayment(
  public val amount: Double,
  public val paymentMethod: String,
  public val paymentDate: LocalDate,
  @TargetEntityId
  public val studentId: String,
)
