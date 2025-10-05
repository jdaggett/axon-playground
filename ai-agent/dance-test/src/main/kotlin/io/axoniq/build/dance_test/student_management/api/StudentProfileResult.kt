package io.axoniq.build.dance_test.student_management.api

import kotlin.Boolean
import kotlin.String

public data class StudentProfileResult(
  public val success: Boolean,
  public val studentId: String,
)
