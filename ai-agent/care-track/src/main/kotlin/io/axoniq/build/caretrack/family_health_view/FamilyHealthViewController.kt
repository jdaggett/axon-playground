package io.axoniq.build.caretrack.family_health_view

import io.axoniq.build.caretrack.family_health_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for Family Health View - exposes family health permission queries via REST API.
 * Part of the Family Health View component for web-based access to permitted patient health information.
 */
@RestController
@RequestMapping("/api/family-health")
class FamilyHealthViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FamilyHealthViewController::class.java)
    }
    
    /**
     * REST endpoint to get detailed permitted health data for a specific health area.
     * Exposes the DetailedPermittedHealthData query from the Family Health View component.
     */
    @GetMapping("/detailed")
    fun getDetailedPermittedHealthData(
        @RequestParam patientId: String,
        @RequestParam familyMemberEmail: String,
        @RequestParam healthArea: String
    ): CompletableFuture<DetailedPermittedHealthDataResult> {
        logger.info("REST request for detailed health data - patient: $patientId, family member: $familyMemberEmail, health area: $healthArea")

        val query = DetailedPermittedHealthData(
            patientId = patientId,
            familyMemberEmail = familyMemberEmail,
            healthArea = healthArea
        )
        return queryGateway.query(query, DetailedPermittedHealthDataResult::class.java, null)
    }

    /**
     * REST endpoint to get permitted patient health information.
     * Exposes the PermittedPatientHealthInfo query from the Family Health View component.
     */
    @GetMapping("/patient/{patientId}")
    fun getPermittedPatientHealthInfo(
        @PathVariable patientId: String,
        @RequestParam familyMemberEmail: String
    ): CompletableFuture<PermittedPatientHealthInfoResult> {
        logger.info("REST request for patient health info - patient: $patientId, family member: $familyMemberEmail")

        val query = PermittedPatientHealthInfo(
            patientId = patientId,
            familyMemberEmail = familyMemberEmail
        )
        return queryGateway.query(query, PermittedPatientHealthInfoResult::class.java, null)
    }
}