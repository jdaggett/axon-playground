package io.axoniq.build.caretrack.medical_record_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "PrescribeTreatment",
  namespace = "caretrack",
)
public data class PrescribeTreatment(
  public val doctorId: String,
  public val frequency: String,
  public val dosage: String,
  @TargetEntityId
  public val patientId: String,
  public val medicationName: String,
  public val duration: String,
)
