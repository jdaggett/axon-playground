package io.axoniq.build.apex_racing_labs.race_profile_view

import io.axoniq.build.apex_racing_labs.race_profile_view.api.RaceProfile
import io.axoniq.build.apex_racing_labs.race_profile_view.api.RaceProfileResult
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the Race Profile View component.
 * Provides HTTP endpoints to access race profile information including ratings and participating drivers.
 */
@RestController
@RequestMapping("/api/race-profiles")
class RaceProfileViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RaceProfileViewController::class.java)
    }

    /**
     * Retrieves detailed race profile information by race ID.
     * Returns race information with ratings, comments, and participating drivers.
     */
    @GetMapping("/{raceId}")
    fun getRaceProfile(@PathVariable raceId: String): CompletableFuture<RaceProfileResult> {
        logger.info("REST request for race profile with raceId: $raceId")

        val query = RaceProfile(raceId)
        return queryGateway.query(query, RaceProfileResult::class.java, null)
    }
}