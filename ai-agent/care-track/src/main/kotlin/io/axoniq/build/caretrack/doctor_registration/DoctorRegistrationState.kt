package io.axoniq.build.caretrack.doctor_registration

import io.axoniq.build.caretrack.doctor_registration.api.DoctorRegistered
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced entity that maintains the state for the Doctor Registration Service component.
 * This state is built from past events to support command validation and decision-making.
 */
@EventSourcedEntity
class DoctorRegistrationState {
    private var email: String? = null
    private var status: String = "NOT_REGISTERED"

    /**
     * Gets the registered email address for the doctor.
     * @return The doctor's email address, or null if not registered
     */
    fun getEmail(): String? = email

    /**
     * Gets the current registration status of the doctor.
     * @return The current status (e.g., "NOT_REGISTERED", "REGISTERED")
     */
    fun getStatus(): String = status

    /**
     * Default constructor for creating a new DoctorRegistrationState entity.
     */
    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for DoctorRegistered events.
     * Updates the state when a doctor has been successfully registered.
     * 
     * @param event The DoctorRegistered event containing registration details
     */
    @EventSourcingHandler
    fun evolve(event: DoctorRegistered) {
        this.email = event.email
        this.status = "REGISTERED"
    }
}

