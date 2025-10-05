package io.axoniq.build.caretrack.patient_health_view

import io.axoniq.build.caretrack.patient_health_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the Patient Health View component.
 * Exposes REST endpoints for querying patient health information including
 * personal health dashboard and detailed health information.
 */
@RestController
@RequestMapping("/api/patient-health")
class PatientHealthViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PatientHealthViewController::class.java)
    }

    /**
     * REST endpoint to retrieve a patient's personal health dashboard.
     * Returns comprehensive health information including active treatments, upcoming appointments, and recent diagnoses.
     */
    @GetMapping("/{patientId}/dashboard")
    fun getPersonalHealthDashboard(@PathVariable patientId: String): CompletableFuture<PersonalHealthDashboardResult> {
        logger.info("REST request for personal health dashboard for patient: $patientId")
        val query = PersonalHealthDashboard(patientId)
        return queryGateway.query(query, PersonalHealthDashboardResult::class.java, null)
    }

    /**
     * REST endpoint to retrieve detailed health information for a specific health area.
     * Returns detailed information about a specific aspect of the patient's health.
     */
    @GetMapping("/{patientId}/details")
    fun getDetailedHealthInformation(
        @PathVariable patientId: String,
        @RequestParam healthArea: String
    ): CompletableFuture<DetailedHealthInformationResult> {
        logger.info("REST request for detailed health information for patient: $patientId, health area: $healthArea")
        val query = DetailedHealthInformation(patientId, healthArea)
        return queryGateway.query(query, DetailedHealthInformationResult::class.java, null)
    }
}