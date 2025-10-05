package io.axoniq.build.dance_test.instructor_management

import io.axoniq.build.dance_test.instructor_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * InstructorManagementState - Event-sourced entity that maintains the state for Instructor Management component.
 * 
 * This entity tracks instructor profile information, including email, phone, specialties, 
 * and Calendly integration status for each instructor.
 */
@EventSourcedEntity
class InstructorManagementState {
    private var instructorId: String? = null
    private var email: String? = null
    private var phone: String? = null
    private var specialties: List<String> = emptyList()
    private var calendlyAccountId: String? = null
    private var calendlyIntegrationStatus: String = "NOT_CONNECTED"

    // Getters for accessing the current state
    fun getInstructorId(): String? = instructorId
    fun getEmail(): String? = email
    fun getPhone(): String? = phone
    fun getSpecialties(): List<String> = specialties
    fun getCalendlyAccountId(): String? = calendlyAccountId
    fun getCalendlyIntegrationStatus(): String = calendlyIntegrationStatus

    @EntityCreator
    constructor()

    /**
     * Handles InstructorProfileCreated event to initialize instructor profile state.
     */
    @EventSourcingHandler
    fun evolve(event: InstructorProfileCreated) {
        this.instructorId = event.instructorId
        this.email = event.email
        this.phone = event.phone
        this.specialties = event.specialties
    }

    /**
     * Handles CalendlyIntegrationConnected event to update Calendly integration status.
     */
    @EventSourcingHandler
    fun evolve(event: CalendlyIntegrationConnected) {
        this.calendlyAccountId = event.calendlyAccountId
        this.calendlyIntegrationStatus = "CONNECTED"
    }

    /**
     * Handles CalendlySettingsUpdated event to update Calendly account information.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: CalendlySettingsUpdated) {
        this.calendlyAccountId = event.calendlyAccountId
    }

    companion object {
        /**
         * Builds EventCriteria for loading instructor-related events from the event store.
         *
         * @param instructorId The instructor identifier to load events for
         * @return EventCriteria that matches all instructor-related events
         */
        @EventCriteriaBuilder
        fun resolveCriteria(instructorId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Instructor", instructorId))
                .andBeingOneOfTypes(
                    InstructorProfileCreated::class.java.name,
                    CalendlyIntegrationConnected::class.java.name,
                    CalendlySettingsUpdated::class.java.name
                )
        }
    }
}