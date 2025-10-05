package io.axoniq.build.jupiter_wheels.bikes_view.api

import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "AllBikesInFleet",
  namespace = "jupiter-wheels",
)
public class AllBikesInFleet()
