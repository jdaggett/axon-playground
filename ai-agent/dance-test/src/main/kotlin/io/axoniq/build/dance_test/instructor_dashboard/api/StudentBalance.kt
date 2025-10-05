package io.axoniq.build.dance_test.instructor_dashboard.api

import java.time.LocalDate
import kotlin.Double
import kotlin.String

public data class StudentBalance(
  public val studentId: String,
  public val lastPaymentDate: LocalDate?,
  public val balance: Double,
  public val studentName: String,
)
