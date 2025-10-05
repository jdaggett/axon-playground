package io.axoniq.build.caretrack.patient_health_view.api

import kotlin.String
import kotlin.collections.List

public data class PersonalHealthDashboardResult(
  public val activeTreatments: List<TreatmentSummary>,
  public val patientName: String,
  public val upcomingAppointments: List<AppointmentSummary>,
  public val recentDiagnoses: List<DiagnosisSummary>,
)
