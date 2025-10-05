package io.axoniq.build.caretrack.account_deletion_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "AccountDeletionInformation",
  namespace = "caretrack",
)
public data class AccountDeletionInformation(
  public val patientId: String,
)
