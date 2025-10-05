package io.axoniq.build.dance_test.communication_management.api

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "FinancialRecordsExported",
  namespace = "dance-test",
)
public data class FinancialRecordsExported(
  public val startDate: LocalDate,
  public val endDate: LocalDate,
  public val exportFormat: String,
  public val exportDate: LocalDateTime,
  @EventTag(key = "Instructor")
  public val instructorId: String,
)
