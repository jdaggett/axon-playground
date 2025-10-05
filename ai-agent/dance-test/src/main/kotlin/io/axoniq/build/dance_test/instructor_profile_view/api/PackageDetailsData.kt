package io.axoniq.build.dance_test.instructor_profile_view.api

import java.time.LocalDate
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String

public data class PackageDetailsData(
  public val lessonCount: Int,
  public val lessonDuration: Int,
  public val isActive: Boolean,
  public val studentId: String,
  public val packageId: String,
  public val creationDate: LocalDate,
  public val price: Double,
)
