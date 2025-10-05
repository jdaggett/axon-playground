package io.axoniq.build.dance_test.session_calendar_view.api

import java.time.LocalDate
import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "InstructorCalendar",
  namespace = "dance-test",
)
public data class InstructorCalendar(
  public val instructorId: String,
  public val startDate: LocalDate,
  public val endDate: LocalDate,
)
