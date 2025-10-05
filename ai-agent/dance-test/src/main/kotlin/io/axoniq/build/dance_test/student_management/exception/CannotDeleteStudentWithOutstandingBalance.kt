package io.axoniq.build.dance_test.student_management.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class CannotDeleteStudentWithOutstandingBalance(
  message: String,
) : IllegalArgumentException(message)
