package io.axoniq.build.caretrack.account_deletion_service.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "DeleteDoctorAccount",
  namespace = "caretrack",
)
public data class DeleteDoctorAccount(
  public val confirmationCode: String,
  @TargetEntityId
  public val doctorId: String,
)
