package io.axoniq.build.caretrack.account_deletion_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DoctorAccountDeletionInformation",
  namespace = "caretrack",
)
public data class DoctorAccountDeletionInformation(
  public val doctorId: String,
)
