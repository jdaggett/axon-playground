package io.axoniq.build.caretrack.family_health_view.api

import java.time.LocalDate
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PatientDiagnosisRecorded",
  namespace = "caretrack",
)
public data class PatientDiagnosisRecorded(
  public val doctorId: String,
  @EventTag(key = "Patient")
  public val patientId: String,
  public val severity: String,
  public val notes: String?,
  public val diagnosisId: String,
  public val condition: String,
  public val diagnosisDate: LocalDate,
)
