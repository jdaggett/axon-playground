package io.axoniq.build.dance_test.student_roster_view.api

import kotlin.Double
import kotlin.Int
import kotlin.String

public data class StudentDetailsData(
  public val monetaryBalance: Double,
  public val relationshipStatus: String,
  public val studentId: String,
  public val name: String,
  public val lessonBalance: Int,
  public val bookingAccessStatus: String,
  public val phone: String,
)
