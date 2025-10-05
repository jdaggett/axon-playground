package io.axoniq.build.sleep_on_time.booking_overview.api

import java.time.LocalDateTime
import kotlin.Boolean
import kotlin.String

public data class BookingOverviewDetails(
  public val containerLocation: String,
  public val checkInTime: LocalDateTime?,
  public val checkOutTime: LocalDateTime?,
  public val bookingId: String,
  public val hasReportedIssues: Boolean,
  public val containerId: String,
  public val canOpenDoor: Boolean,
  public val status: String,
)
