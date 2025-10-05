package io.axoniq.build.caretrack.patient_registration.api

import java.time.LocalDate
import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "RegisterPatient",
  namespace = "caretrack",
)
public data class RegisterPatient(
  public val firstName: String,
  public val email: String,
  public val dateOfBirth: LocalDate,
  public val lastName: String,
)
