package io.axoniq.build.sleep_on_time.issue_reporting.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class InvalidIssueData(
  message: String,
) : IllegalArgumentException(message)
