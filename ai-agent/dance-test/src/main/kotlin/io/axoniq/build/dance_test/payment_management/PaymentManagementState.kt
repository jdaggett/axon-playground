package io.axoniq.build.dance_test.payment_management

import io.axoniq.build.dance_test.payment_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import java.math.BigDecimal

/**
 * Event-sourced entity representing the payment management state for a student.
 * Tracks monetary balance and lesson balance for payment management.
 */
@EventSourcedEntity
class PaymentManagementState {

    private var monetaryBalance: BigDecimal = BigDecimal.ZERO
    private var lessonBalance: Int = 0
    private var studentId: String? = null

    fun getMonetaryBalance(): BigDecimal = monetaryBalance
    fun getLessonBalance(): Int = lessonBalance
    fun getStudentId(): String? = studentId

    @EntityCreator
    constructor()

    /**
     * Handles PaymentRecorded events to update the payment state.
     * Records the payment information but does not affect balances directly.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: PaymentRecorded) {
        this.studentId = event.studentId
    }

    /**
     * Handles MonetaryBalanceIncreasedFromPayment events to increase the monetary balance.
     */
    @EventSourcingHandler
    fun evolve(event: MonetaryBalanceIncreasedFromPayment) {
        this.studentId = event.studentId
        this.monetaryBalance = this.monetaryBalance.add(BigDecimal.valueOf(event.amount))
    }

    /**
     * HandlesBalanceAdjustmentRecorded events to adjust the monetary balance.
     */
    @EventSourcingHandler
    fun evolve(event: BalanceAdjustmentRecorded) {
        this.studentId = event.studentId
        this.monetaryBalance = this.monetaryBalance.add(BigDecimal.valueOf(event.adjustmentAmount))
    }

    /**
     * Handles LessonBalanceIncreasedFromPackage events to increase the lesson balance.
     */
    @EventSourcingHandler
    fun evolve(event: LessonBalanceIncreasedFromPackage) {
        this.studentId = event.studentId
        this.lessonBalance += event.lessonCount
    }

    /**
     * Handles LessonBalanceDecreasedFromSession events to decrease the lesson balance.
     */
    @EventSourcingHandler
    fun evolve(event: LessonBalanceDecreasedFromSession) {
        this.studentId = event.studentId
        this.lessonBalance -= event.lessonsUsed
    }

    companion object {
        /**
         * Builds event criteria for loading payment management events for a specific student.
         * 
         * @param studentId The student identifier
         * @return EventCriteria for querying student payment events
         */
        @EventCriteriaBuilder
        fun resolveCriteria(studentId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Student", studentId))
                .andBeingOneOfTypes(
                    PaymentRecorded::class.java.name,
                    MonetaryBalanceIncreasedFromPayment::class.java.name,
                    BalanceAdjustmentRecorded::class.java.name,
                    LessonBalanceIncreasedFromPackage::class.java.name,
                    LessonBalanceDecreasedFromSession::class.java.name
                )
        }
    }
}

