package io.axoniq.build.dance_test.booking_access_management

import io.axoniq.build.dance_test.booking_access_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * BookingAccessManagementState - Event sourced entity for booking access management
 * Maintains the state for student booking access status and related properties
 */
@EventSourcedEntity
class BookingAccessManagementState {
    private var hasExistingBalance: Boolean = false
    private var bookingAccessStatus: String = ""
    private var blockingReason: String? = null
    private var studentId: String = ""
    private var instructorId: String = ""

    fun getHasExistingBalance(): Boolean = hasExistingBalance
    fun getBookingAccessStatus(): String = bookingAccessStatus
    fun getBlockingReason(): String? = blockingReason
    fun getStudentId(): String = studentId
    fun getInstructorId(): String = instructorId

    @EntityCreator
    constructor()

    /**
     * Handles BookingAccessStatusUpdated event - updates the booking access status for a student
     */
    @EventSourcingHandler
    fun evolve(event: BookingAccessStatusUpdated) {
        this.instructorId = event.instructorId
        this.bookingAccessStatus = event.newAccessStatus
        this.blockingReason = event.reason
        this.studentId = event.studentId
    }

    /**
     * Handles BlockingBalanceDecisionRecorded event - records the balance preservation decision
     */
    @EventSourcingHandler
    fun evolve(event: BlockingBalanceDecisionRecorded) {
        this.instructorId = event.instructorId
        this.hasExistingBalance = event.preserveBalances
        this.studentId = event.studentId
    }

    /**
     * Handles BookingAccessBlocked event - blocks booking access for a student
     */
    @EventSourcingHandler
    fun evolve(event: BookingAccessBlocked) {
        this.instructorId = event.instructorId
        this.blockingReason = event.blockingReason
        this.studentId = event.studentId
        this.bookingAccessStatus = "BLOCKED"
    }

    companion object {
        /**
         * Builds event criteria to load events for the booking access management state
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Student", id))
                .andBeingOneOfTypes(
                    BookingAccessStatusUpdated::class.java.name,
                    BlockingBalanceDecisionRecorded::class.java.name,
                    BookingAccessBlocked::class.java.name
                )
        }
    }
}