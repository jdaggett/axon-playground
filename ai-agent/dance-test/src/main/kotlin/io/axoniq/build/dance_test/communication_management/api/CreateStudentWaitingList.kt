package io.axoniq.build.dance_test.communication_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateStudentWaitingList",
  namespace = "dance-test",
)
public data class CreateStudentWaitingList(
  @TargetEntityId
  public val instructorId: String,
)
