package io.axoniq.build.caretrack.account_deletion_service.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "DeletePatientAccount",
  namespace = "caretrack",
)
public data class DeletePatientAccount(
  public val confirmationCode: String,
  @TargetEntityId
  public val patientId: String,
)
