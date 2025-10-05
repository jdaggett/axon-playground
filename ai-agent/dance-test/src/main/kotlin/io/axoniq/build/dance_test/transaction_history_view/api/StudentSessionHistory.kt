package io.axoniq.build.dance_test.transaction_history_view.api

import java.time.LocalDate
import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "StudentSessionHistory",
  namespace = "dance-test",
)
public data class StudentSessionHistory(
  public val startDate: LocalDate?,
  public val studentId: String,
  public val endDate: LocalDate?,
)
