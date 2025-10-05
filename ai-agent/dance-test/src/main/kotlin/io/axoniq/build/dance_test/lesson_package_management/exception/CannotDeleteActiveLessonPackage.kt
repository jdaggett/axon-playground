package io.axoniq.build.dance_test.lesson_package_management.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class CannotDeleteActiveLessonPackage(
  message: String,
) : IllegalArgumentException(message)
