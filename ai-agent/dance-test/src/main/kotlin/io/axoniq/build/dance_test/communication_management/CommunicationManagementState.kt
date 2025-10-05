package io.axoniq.build.dance_test.communication_management

import io.axoniq.build.dance_test.communication_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced entity for Communication Management component.
 * Maintains state for payment reminders and instructor-student communication.
 */
@EventSourcedEntity
class CommunicationManagementState {

    private var remindersSent: Int = 0
    private var studentId: String? = null
    private var waitingListExists: Boolean = false
    private var instructorId: String = ""

    fun getRemindersSent(): Int = remindersSent
    fun getStudentId(): String? = studentId
    fun getWaitingListExists(): Boolean = waitingListExists
    fun getInstructorId(): String = instructorId

    @EntityCreator
    constructor()

    /**
     * Handles WaitingListCreated event to update state when a waiting list is created.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: WaitingListCreated) {
        this.waitingListExists = true
        this.instructorId = event.instructorId
    }

    /**
     * Handles FinancialRecordsExported event to update state when financial records are exported.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: FinancialRecordsExported) {
        this.instructorId = event.instructorId
    }

    /**
     * Handles PaymentReminderSent event to update state when a payment reminder is sent.
     */
    @EventSourcingHandler
    fun evolve(event: PaymentReminderSent) {
        this.remindersSent++
        this.studentId = event.studentId
        this.instructorId = event.instructorId
    }

    companion object {
        /**
         * Builds event criteria for loading communication management events.
         * Loads events tagged with the instructor or student identifiers.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(instructorId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Instructor", instructorId))
                .andBeingOneOfTypes(
                    WaitingListCreated::class.java.name,
                    FinancialRecordsExported::class.java.name,
                    PaymentReminderSent::class.java.name
                )
        }
    }
}

