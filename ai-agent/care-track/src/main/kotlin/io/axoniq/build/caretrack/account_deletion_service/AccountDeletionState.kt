package io.axoniq.build.caretrack.account_deletion_service

import io.axoniq.build.caretrack.account_deletion_service.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Event-sourced entity for Account Deletion Service
 * Maintains state of account deletion operations
 */
@EventSourcedEntity
class AccountDeletionState {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AccountDeletionState::class.java)

        /**
         * Builds event criteria for loading account deletion events
         * Loads both patient and doctor account deletion events
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria.either(
                EventCriteria
                    .havingTags(Tag.of("Patient", id))
                    .andBeingOneOfTypes(
                        PatientAccountDeleted::class.java.name
                    ),
                EventCriteria
                    .havingTags(Tag.of("Doctor", id))
                    .andBeingOneOfTypes(
                        DoctorAccountDeleted::class.java.name
                    )
            )
        }
    }

    private var accountType: String = ""
    private var accountId: String = ""
    private var status: String = "ACTIVE"

    fun getAccountType(): String = accountType
    fun getAccountId(): String = accountId
    fun getStatus(): String = status

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for PatientAccountDeleted event
     * Updates state to reflect patient account deletion
     */
    @EventSourcingHandler
    fun evolve(event: PatientAccountDeleted) {
        logger.debug("Evolving state for PatientAccountDeleted event: ${event.patientId}")
        this.accountType = "PATIENT"
        this.accountId = event.patientId
        this.status = "DELETED"
    }

    /**
     * Event sourcing handler for DoctorAccountDeleted event
     * Updates state to reflect doctor account deletion
     */
    @EventSourcingHandler
    fun evolve(event: DoctorAccountDeleted) {
        logger.debug("Evolving state for DoctorAccountDeleted event: ${event.doctorId}")
        this.accountType = "DOCTOR"
        this.accountId = event.doctorId
        this.status = "DELETED"
    }
}

