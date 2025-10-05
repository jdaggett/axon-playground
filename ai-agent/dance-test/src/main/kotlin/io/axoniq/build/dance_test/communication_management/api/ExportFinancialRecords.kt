package io.axoniq.build.dance_test.communication_management.api

import java.time.LocalDate
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ExportFinancialRecords",
  namespace = "dance-test",
)
public data class ExportFinancialRecords(
  public val startDate: LocalDate,
  public val endDate: LocalDate,
  public val exportFormat: String,
  @TargetEntityId
  public val instructorId: String,
)
