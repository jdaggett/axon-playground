package io.axoniq.build.dance_test.instructor_dashboard.api

import kotlin.Int
import kotlin.collections.List

public data class InstructorDashboardData(
  public val upcomingSessions: List<SessionSummary>,
  public val recentActivity: List<ActivitySummary>,
  public val totalActiveStudents: Int,
)
