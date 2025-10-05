package io.axoniq.build.caretrack.account_deletion_view.api

import kotlin.String

public data class AccountDeletionInformationResult(
  public val impactWarning: String,
  public val dataRetentionPeriod: String,
  public val deletionRequirements: String,
)
