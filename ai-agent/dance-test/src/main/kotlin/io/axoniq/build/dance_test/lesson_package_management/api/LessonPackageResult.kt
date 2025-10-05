package io.axoniq.build.dance_test.lesson_package_management.api

import kotlin.Boolean
import kotlin.String

public data class LessonPackageResult(
  public val success: Boolean,
  public val packageId: String,
)
