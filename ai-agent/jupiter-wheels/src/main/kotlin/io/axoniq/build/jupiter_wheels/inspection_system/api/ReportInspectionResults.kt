package io.axoniq.build.jupiter_wheels.inspection_system.api

import kotlin.Boolean
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ReportInspectionResults",
  namespace = "jupiter-wheels",
)
public data class ReportInspectionResults(
  public val inspectionPassed: Boolean,
  public val issues: String?,
  @TargetEntityId
  public val rentalId: String,
)
