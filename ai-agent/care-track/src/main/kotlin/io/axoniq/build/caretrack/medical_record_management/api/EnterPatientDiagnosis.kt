package io.axoniq.build.caretrack.medical_record_management.api

import java.time.LocalDate
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "EnterPatientDiagnosis",
  namespace = "caretrack",
)
public data class EnterPatientDiagnosis(
  public val doctorId: String,
  @TargetEntityId
  public val patientId: String,
  public val severity: String,
  public val notes: String?,
  public val condition: String,
  public val diagnosisDate: LocalDate,
)
