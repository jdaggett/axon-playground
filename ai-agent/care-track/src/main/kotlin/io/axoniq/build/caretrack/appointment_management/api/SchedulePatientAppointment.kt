package io.axoniq.build.caretrack.appointment_management.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "SchedulePatientAppointment",
  namespace = "caretrack",
)
public data class SchedulePatientAppointment(
  public val doctorId: String,
  public val purpose: String,
  @TargetEntityId
  public val patientId: String,
  public val appointmentDate: LocalDateTime,
)
