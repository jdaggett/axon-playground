package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Int
import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "WorkloadAnalysis",
  namespace = "dance-test",
)
public data class WorkloadAnalysis(
  public val instructorId: String,
  public val periodWeeks: Int,
)
