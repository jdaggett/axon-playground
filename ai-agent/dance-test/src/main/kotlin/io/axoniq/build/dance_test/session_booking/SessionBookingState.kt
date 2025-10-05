package io.axoniq.build.dance_test.session_booking

import io.axoniq.build.dance_test.session_booking.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Event-sourced entity for Session Booking component that maintains the in-memory model
 * based on past events for session scheduling, cancellation and negative balance booking.
 */
@EventSourcedEntity
class SessionBookingState {
    private var duration: Int = 0
    private var sessionDate: LocalDateTime? = null
    private var studentBalance: BigDecimal = BigDecimal.ZERO
    private var studentId: String? = null
    private var creditLimit: BigDecimal = BigDecimal.ZERO
    private var status: String = "PENDING"
    private var cancellationTime: LocalDateTime? = null
    private var instructorId: String? = null
    private var sessionId: String? = null

    @EntityCreator
    constructor()

    // Getters for command handlers to access state
    fun getDuration(): Int = duration
    fun getSessionDate(): LocalDateTime? = sessionDate
    fun getStudentBalance(): BigDecimal = studentBalance
    fun getStudentId(): String? = studentId
    fun getCreditLimit(): BigDecimal = creditLimit
    fun getStatus(): String = status
    fun getCancellationTime(): LocalDateTime? = cancellationTime
    fun getInstructorId(): String? = instructorId
    fun getSessionId(): String? = sessionId

    @EventSourcingHandler
    fun evolve(event: SessionScheduled) {
        this.instructorId = event.instructorId
        this.duration = event.duration
        this.sessionDate = event.sessionDate
        this.studentId = event.studentId
        this.sessionId = event.sessionId
        this.status = "SCHEDULED"
    }

    @EventSourcingHandler
    fun evolve(event: SessionScheduledWithNegativeBalance) {
        this.studentId = event.studentId
        this.studentBalance = BigDecimal.valueOf(event.negativeBalance)
        this.sessionId = event.sessionId
        this.status = "SCHEDULED_NEGATIVE_BALANCE"
    }

    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: SessionScheduledBeyondLimit) {
        this.status = "SCHEDULED_BEYOND_LIMIT"
    }

    @EventSourcingHandler
    fun evolve(event: SessionCancelled) {
        this.cancellationTime = event.cancellationTime
        this.sessionId = event.sessionId
        this.status = "CANCELLED"
    }

    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: LessonForfeitedForLateCancellation) {
        this.status = "FORFEITED"
    }

    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: CancellationDecisionRecorded) {
        this.status = "DECISION_RECORDED"
    }

    @EventSourcingHandler
    fun evolve(event: DebtAcknowledgmentRecorded) {
        this.studentBalance = this.studentBalance.add(BigDecimal.valueOf(event.acknowledgedAmount))
        this.status = "DEBT_ACKNOWLEDGED"
    }

    companion object {
        @EventCriteriaBuilder
        fun resolveCriteria(sessionId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Session", sessionId))
                .andBeingOneOfTypes(
                    SessionScheduled::class.java.name,
                    SessionScheduledWithNegativeBalance::class.java.name,
                    SessionScheduledBeyondLimit::class.java.name,
                    SessionCancelled::class.java.name,
                    LessonForfeitedForLateCancellation::class.java.name,
                    CancellationDecisionRecorded::class.java.name,
                    DebtAcknowledgmentRecorded::class.java.name
                )
        }
    }
}

