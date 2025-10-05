package io.axoniq.build.caretrack.family_health_view.api

import kotlin.String
import kotlin.collections.List

public data class PermittedPatientHealthInfoResult(
  public val permittedDiagnoses: List<DiagnosisSummary>,
  public val permittedTreatments: List<TreatmentSummary>,
  public val patientName: String,
  public val permittedAppointments: List<AppointmentSummary>,
)
