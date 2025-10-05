package io.axoniq.build.dance_test.instructor_management.api

import kotlin.Boolean
import kotlin.String

public data class InstructorProfileResult(
  public val instructorId: String,
  public val success: Boolean,
)
