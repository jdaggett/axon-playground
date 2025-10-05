package io.axoniq.build.caretrack.account_deletion_view.api

import kotlin.String

public data class DoctorAccountDeletionInformationResult(
  public val impactWarning: String,
  public val dataRetentionPeriod: String,
  public val deletionRequirements: String,
)
