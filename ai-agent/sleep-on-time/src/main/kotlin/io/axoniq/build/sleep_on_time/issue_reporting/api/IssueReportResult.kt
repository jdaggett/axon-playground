package io.axoniq.build.sleep_on_time.issue_reporting.api

import kotlin.Boolean
import kotlin.String

public data class IssueReportResult(
  public val success: Boolean,
  public val issueId: String,
)
