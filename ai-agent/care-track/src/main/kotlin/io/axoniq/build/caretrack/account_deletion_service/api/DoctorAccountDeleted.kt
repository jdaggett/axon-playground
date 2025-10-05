package io.axoniq.build.caretrack.account_deletion_service.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "DoctorAccountDeleted",
  namespace = "caretrack",
)
public data class DoctorAccountDeleted(
  @EventTag(key = "Doctor")
  public val doctorId: String,
)
