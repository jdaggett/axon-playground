package io.axoniq.build.caretrack.patient_health_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PatientDiagnosisRemoved",
  namespace = "caretrack",
)
public data class PatientDiagnosisRemoved(
  public val doctorId: String,
  @EventTag(key = "Patient")
  public val patientId: String,
  public val diagnosisId: String,
)
