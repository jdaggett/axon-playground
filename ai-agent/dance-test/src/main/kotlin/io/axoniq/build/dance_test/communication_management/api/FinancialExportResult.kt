package io.axoniq.build.dance_test.communication_management.api

import kotlin.Boolean
import kotlin.String

public data class FinancialExportResult(
  public val success: Boolean,
  public val exportFileUrl: String,
)
