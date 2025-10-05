package io.axoniq.build.dance_test.instructor_profile_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "PackageDetails",
  namespace = "dance-test",
)
public data class PackageDetails(
  public val packageId: String,
)
