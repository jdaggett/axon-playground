package io.axoniq.build.caretrack.medical_record_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "DiscontinueTreatment",
  namespace = "caretrack",
)
public data class DiscontinueTreatment(
  public val doctorId: String,
  public val reason: String?,
  @TargetEntityId
  public val patientId: String,
  public val treatmentId: String,
)
