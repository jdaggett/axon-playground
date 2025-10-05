package io.axoniq.build.caretrack.patient_registration

import io.axoniq.build.caretrack.patient_registration.api.*
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * Event-sourced entity state for the Patient Registration Service component.
 * This state maintains the current registration status and patient email information
 * based on the events that have occurred in the patient registration domain.
 */
@EventSourcedEntity
class PatientRegistrationServiceState {

    private var email: String = ""
    private var status: String = "NEW"

    /**
     * Gets the current email address of the patient.
     * @return The patient's email address
     */
    fun getEmail(): String = email

    /**
     * Gets the current registration status of the patient.
     * @return The current status (NEW, REGISTERED, etc.)
     */
    fun getStatus(): String = status
    
    /**
     * Default constructor for creating new patient registration state instances.
     */
    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for PatientRegistered events.
     * Updates the patient registration state when a patient is successfully registered.
     * 
     * @param event The PatientRegistered event containing patient registration details
     */
    @EventSourcingHandler
    fun evolve(event: PatientRegistered) {
        this.email = event.email
        this.status = "REGISTERED"
    }
}

