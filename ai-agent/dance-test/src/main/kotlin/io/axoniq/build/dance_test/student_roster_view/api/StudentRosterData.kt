package io.axoniq.build.dance_test.student_roster_view.api

import kotlin.collections.List

public data class StudentRosterData(
  public val students: List<StudentSummary>,
)
