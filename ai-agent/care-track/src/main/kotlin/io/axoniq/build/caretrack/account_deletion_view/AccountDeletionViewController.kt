package io.axoniq.build.caretrack.account_deletion_view

import io.axoniq.build.caretrack.account_deletion_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for Account Deletion View component
 * Exposes endpoints to retrieve account deletion information for patients and doctors
 */
@RestController
@RequestMapping("/api/account-deletion")
class AccountDeletionViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AccountDeletionViewController::class.java)
    }

    /**
     * Get account deletion information for a patient
     * @param patientId the patient identifier
     * @return CompletableFuture with AccountDeletionInformationResult
     */
    @GetMapping("/patient/{patientId}")
    fun getPatientAccountDeletionInfo(@PathVariable patientId: String): CompletableFuture<AccountDeletionInformationResult> {
        logger.info("Received request for patient account deletion information: {}", patientId)
        val query = AccountDeletionInformation(patientId)
        return queryGateway.query(query, AccountDeletionInformationResult::class.java, null)
    }

    /**
     * Get account deletion information for a doctor
     * @param doctorId the doctor identifier
     * @return CompletableFuture with DoctorAccountDeletionInformationResult
     */
    @GetMapping("/doctor/{doctorId}")
    fun getDoctorAccountDeletionInfo(@PathVariable doctorId: String): CompletableFuture<DoctorAccountDeletionInformationResult> {
        logger.info("Received request for doctor account deletion information: {}", doctorId)
        val query = DoctorAccountDeletionInformation(doctorId)
        return queryGateway.query(query, DoctorAccountDeletionInformationResult::class.java, null)
    }
}