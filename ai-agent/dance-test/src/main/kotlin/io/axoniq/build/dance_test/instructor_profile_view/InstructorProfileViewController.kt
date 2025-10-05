package io.axoniq.build.dance_test.instructor_profile_view

import io.axoniq.build.dance_test.instructor_profile_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for instructor profile view operations.
 * Exposes endpoints to query instructor profile data and package details.
 */
@RestController
@RequestMapping("/api/instructor-profiles")
class InstructorProfileViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InstructorProfileViewController::class.java)
    }

    /**
     * REST endpoint to retrieve instructor profile settings by instructor ID.
     */
    @GetMapping("/{instructorId}")
    fun getInstructorProfile(@PathVariable instructorId: String): CompletableFuture<InstructorProfileData> {
        logger.info("REST request for instructor profile with ID: $instructorId")
        val query = InstructorProfileSettings(instructorId)
        return queryGateway.query(query, InstructorProfileData::class.java, null)
    }

    /**
     * REST endpoint to retrieve package details by package ID.
     */
    @GetMapping("/packages/{packageId}")
    fun getPackageDetails(@PathVariable packageId: String): CompletableFuture<PackageDetailsData> {
        logger.info("REST request for package details with ID: $packageId")
        val query = PackageDetails(packageId)
        return queryGateway.query(query, PackageDetailsData::class.java, null)
    }
}