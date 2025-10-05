package io.axoniq.build.caretrack.medical_record_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RemovePatientDiagnosis",
  namespace = "caretrack",
)
public data class RemovePatientDiagnosis(
  public val doctorId: String,
  @TargetEntityId
  public val patientId: String,
  public val diagnosisId: String,
)
