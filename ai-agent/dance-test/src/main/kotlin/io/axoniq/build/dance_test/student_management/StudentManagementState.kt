package io.axoniq.build.dance_test.student_management

import io.axoniq.build.dance_test.student_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced entity representing the state for Student Management component.
 * Tracks student profile information, trainer relationships, and balance status.
 */
@EventSourcedEntity
class StudentManagementState {

    private var studentId: String? = null
    private var name: String? = null
    private var phone: String? = null
    private var instructorId: String? = null
    private var relationshipStatus: String = "NONE"
    private var hasOutstandingBalance: Boolean = false

    fun getStudentId(): String? = studentId
    fun getName(): String? = name
    fun getPhone(): String? = phone
    fun getInstructorId(): String? = instructorId
    fun getRelationshipStatus(): String = relationshipStatus
    fun getHasOutstandingBalance(): Boolean = hasOutstandingBalance

    @EntityCreator
    constructor()

    /**
     * Handles StudentProfileCreated event to initialize student profile information.
     */
    @EventSourcingHandler
    fun evolve(event: StudentProfileCreated) {
        this.studentId = event.studentId
        this.name = event.name
        this.phone = event.phone
        this.instructorId = event.instructorId
    }

    /**
     * Handles TrainerStudentRelationshipEstablished event to set relationship status.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: TrainerStudentRelationshipEstablished) {
        this.relationshipStatus = "ACTIVE"
    }

    /**
     * Handles TrainerStudentRelationshipTerminated event to update relationship status.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: TrainerStudentRelationshipTerminated) {
        this.relationshipStatus = "TERMINATED"
    }

    /**
     * Handles MonetaryBalanceIncreasedFromPayment event to update balance status.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: MonetaryBalanceIncreasedFromPayment) {
        this.hasOutstandingBalance = false
    }

    /**
     * Handles StudentProfileDeleted event to clear student information.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: StudentProfileDeleted) {
        this.studentId = null
        this.name = null
        this.phone = null
        this.instructorId = null
        this.relationshipStatus = "NONE"
        this.hasOutstandingBalance = false
    }

    companion object {
        /**
         * Builds EventCriteria for loading student management events by student ID.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(studentId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Student", studentId))
                .andBeingOneOfTypes(
                    StudentProfileCreated::class.java.name,
                    StudentProfileDeleted::class.java.name,
                    TrainerStudentRelationshipEstablished::class.java.name,
                    TrainerStudentRelationshipTerminated::class.java.name,
                    MonetaryBalanceIncreasedFromPayment::class.java.name
                )
        }
    }
}

