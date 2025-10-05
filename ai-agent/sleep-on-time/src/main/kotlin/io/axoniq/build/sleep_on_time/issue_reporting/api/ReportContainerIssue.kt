package io.axoniq.build.sleep_on_time.issue_reporting.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ReportContainerIssue",
  namespace = "sleep-on-time",
)
public data class ReportContainerIssue(
  public val bookingId: String,
  public val issueType: String,
  public val guestId: String,
  public val description: String,
  public val severity: String,
  public val containerId: String,
) {
  @TargetEntityId
  public fun modelIdentifier(): TargetIdentifier = TargetIdentifier(bookingId, guestId, containerId)

  public data class TargetIdentifier(
    public val bookingId: String,
    public val guestId: String,
    public val containerId: String,
  )
}
