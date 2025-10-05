package io.axoniq.build.dance_test.student_roster_view.api

import java.time.LocalDate
import kotlin.Double
import kotlin.Int
import kotlin.String

public data class DetailedStudentData(
  public val monetaryBalance: Double,
  public val relationshipStatus: String,
  public val totalSessionsCompleted: Int,
  public val studentId: String,
  public val name: String,
  public val lessonBalance: Int,
  public val bookingAccessStatus: String,
  public val totalLifetimePayments: Double,
  public val phone: String,
  public val lastBookingDate: LocalDate?,
)
