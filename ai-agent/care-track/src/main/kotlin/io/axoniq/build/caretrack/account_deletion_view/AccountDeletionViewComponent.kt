package io.axoniq.build.caretrack.account_deletion_view

import io.axoniq.build.caretrack.account_deletion_view.api.*
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Account Deletion View component - handles account deletion information queries
 * Provides information about account deletion requirements, data retention periods, and impact warnings
 * for both patient and doctor accounts
 */
@Component
class AccountDeletionViewComponent(
    private val repository: AccountDeletionViewRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AccountDeletionViewComponent::class.java)
    }

    /**
     * Handles AccountDeletionInformation query for patient accounts
     * Returns deletion information including impact warning, data retention period, and requirements
     * @param query the AccountDeletionInformation query containing patientId
     * @return AccountDeletionInformationResult with deletion information
     */
    @QueryHandler
    fun handle(query: AccountDeletionInformation): AccountDeletionInformationResult {
        logger.info("Handling AccountDeletionInformation query for patientId: {}", query.patientId)

        val entity = repository.findByAccountId(query.patientId)

        return if (entity != null) {
            AccountDeletionInformationResult(
                impactWarning = "Deleting your patient account will permanently remove all medical records, appointment history, and personal health data. This action cannot be undone.",
                dataRetentionPeriod = "Medical data will be retained for 7 years as required by healthcare regulations, then permanently deleted.",
                deletionRequirements = entity.deletionRequirements
            )
        } else {
            // Default response for accounts not found in the system
            AccountDeletionInformationResult(
                impactWarning = "Account deletion will remove all associated data permanently.",
                dataRetentionPeriod = "Data will be retained according to applicable regulations.",
                deletionRequirements = "Standard account deletion procedures apply."
            )
        }
    }

    /**
     * Handles DoctorAccountDeletionInformation query for doctor accounts
     * Returns deletion information including impact warning, data retention period, and requirements
     * @param query the DoctorAccountDeletionInformation query containing doctorId
     * @return DoctorAccountDeletionInformationResult with deletion information
     */
    @QueryHandler
    fun handle(query: DoctorAccountDeletionInformation): DoctorAccountDeletionInformationResult {
        logger.info("Handling DoctorAccountDeletionInformation query for doctorId: {}", query.doctorId)

        val entity = repository.findByAccountId(query.doctorId)

        return if (entity != null) {
            DoctorAccountDeletionInformationResult(
                impactWarning = "Deleting your doctor account will affect patient access to their medical records and appointment history associated with you. All professional data and patient interactions will be permanently removed.",
                dataRetentionPeriod = "Professional medical records will be retained for 10 years as required by medical licensing regulations, then permanently deleted.",
                deletionRequirements = entity.deletionRequirements
            )
        } else {
            // Default response for accounts not found in the system
            DoctorAccountDeletionInformationResult(
                impactWarning = "Doctor account deletion will remove all professional data and patient associations permanently.",
                dataRetentionPeriod = "Professional data will be retained according to medical licensing regulations.",
                deletionRequirements = "Standard doctor account deletion procedures apply including patient notification requirements."
            )
        }
    }
}