package io.axoniq.build.caretrack.patient_registration.api

import kotlin.Boolean
import kotlin.String

public data class PatientRegistrationResult(
  public val patientId: String,
  public val registrationSuccessful: Boolean,
)
