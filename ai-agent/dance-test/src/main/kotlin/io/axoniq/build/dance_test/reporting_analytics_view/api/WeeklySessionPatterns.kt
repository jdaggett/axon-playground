package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Int
import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "WeeklySessionPatterns",
  namespace = "dance-test",
)
public data class WeeklySessionPatterns(
  public val instructorId: String,
  public val periodWeeks: Int,
)
