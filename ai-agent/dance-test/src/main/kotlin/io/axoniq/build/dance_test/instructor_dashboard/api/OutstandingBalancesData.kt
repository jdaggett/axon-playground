package io.axoniq.build.dance_test.instructor_dashboard.api

import kotlin.collections.List

public data class OutstandingBalancesData(
  public val studentsWithNegativeBalances: List<StudentBalance>,
)
