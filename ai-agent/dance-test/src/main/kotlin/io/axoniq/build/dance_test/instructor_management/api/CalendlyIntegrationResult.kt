package io.axoniq.build.dance_test.instructor_management.api

import kotlin.Boolean
import kotlin.String

public data class CalendlyIntegrationResult(
  public val success: Boolean,
  public val integrationId: String,
)
