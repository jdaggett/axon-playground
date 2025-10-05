package io.axoniq.build.dance_test.session_booking

import io.axoniq.build.dance_test.session_booking.api.*
import io.axoniq.build.dance_test.session_booking.exception.CreditLimitExceeded
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal

/**
 * Command handler for Session Booking component that handles session scheduling,
 * cancellation and negative balance booking operations.
 */
class SessionBookingCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SessionBookingCommandHandler::class.java)
        private val DEFAULT_CREDIT_LIMIT = BigDecimal.valueOf(-100.0)
    }

    /**
     * Handles CreateSessionBooking command to schedule a new session.
     */
    @CommandHandler
    fun handle(
        command: CreateSessionBooking,
        @InjectEntity state: SessionBookingState,
        eventAppender: EventAppender
    ): SessionBookingResult {
        logger.info("Handling CreateSessionBooking command for sessionId: ${command.sessionId}")
        
        val event = SessionScheduled(
            instructorId = command.instructorId,
            duration = command.duration,
            sessionDate = command.sessionDate,
            studentId = command.studentId,
            sessionId = command.sessionId
        )
        eventAppender.append(event)

        return SessionBookingResult(success = true, sessionId = command.sessionId)
    }

    /**
     * Handles CreateNegativeBalanceSession command to schedule a session with negative balance.
     */
    @CommandHandler
    fun handle(
        command: CreateNegativeBalanceSession,
        @InjectEntity state: SessionBookingState,
        eventAppender: EventAppender
    ): NegativeBalanceSessionResult {
        logger.info("Handling CreateNegativeBalanceSession command for sessionId: ${command.sessionId}")

        // Check if the negative balance exceeds credit limit
        val currentBalance = state.getStudentBalance()
        val creditLimit = if (state.getCreditLimit() == BigDecimal.ZERO) DEFAULT_CREDIT_LIMIT else state.getCreditLimit()
        
        if (currentBalance < creditLimit) {
            logger.warn("Credit limit exceeded for student: ${command.studentId}")
            throw CreditLimitExceeded("Student has exceeded credit limit")
        }

        val event = SessionScheduledWithNegativeBalance(
            studentId = command.studentId,
            negativeBalance = currentBalance.toDouble(),
            sessionId = command.sessionId
        )
        eventAppender.append(event)

        return NegativeBalanceSessionResult(success = true, sessionId = command.sessionId)
    }

    /**
     * Handles CancelSessionBooking command to cancel an existing session.
     */
    @CommandHandler
    fun handle(
        command: CancelSessionBooking,
        @InjectEntity state: SessionBookingState,
        eventAppender: EventAppender
    ): SessionCancellationResult {
        logger.info("Handling CancelSessionBooking command for sessionId: ${command.sessionId}")

        // First emit session cancelled event
        val cancelledEvent = SessionCancelled(
            cancellationTime = command.cancellationTime,
            sessionId = command.sessionId
        )
        eventAppender.append(cancelledEvent)
        
        // Check if it's a late cancellation (less than 24 hours before session)
        val sessionDate = state.getSessionDate()
        if (sessionDate != null && command.cancellationTime.isAfter(sessionDate.minusDays(1))) {
            val forfeitEvent = LessonForfeitedForLateCancellation(
                lessonsForfeited = 1,
                studentId = state.getStudentId() ?: "",
                sessionId = command.sessionId
            )
            eventAppender.append(forfeitEvent)
        }

        return SessionCancellationResult(success = true)
    }

    /**
     * Handles DecideCancellationCharges command to record cancellation decision.
     */
    @CommandHandler
    fun handle(
        command: DecideCancellationCharges,
        @InjectEntity state: SessionBookingState,
        eventAppender: EventAppender
    ): CancellationDecisionResult {
        logger.info("Handling DecideCancellationCharges command for sessionId: ${command.sessionId}")

        val event = CancellationDecisionRecorded(
            chargeStudent = command.chargeStudent,
            reason = command.reason,
            sessionId = command.sessionId
        )
        eventAppender.append(event)

        return CancellationDecisionResult(success = true)
    }

    /**
     * Handles AcknowledgeDebtAccumulation command to record debt acknowledgment.
     */
    @CommandHandler
    fun handle(
        command: AcknowledgeDebtAccumulation,
        @InjectEntity state: SessionBookingState,
        eventAppender: EventAppender
    ): DebtAcknowledgmentResult {
        logger.info("Handling AcknowledgeDebtAccumulation command for studentId: ${command.studentId}")

        // First record the debt acknowledgment
        val acknowledgedAmount = state.getStudentBalance().abs().toDouble()
        val debtEvent = DebtAcknowledgmentRecorded(
            acknowledgedAmount = acknowledgedAmount,
            studentId = command.studentId,
            sessionId = command.sessionId
        )
        eventAppender.append(debtEvent)

        // Check if student is scheduling beyond reasonable limit
        val currentBalance = state.getStudentBalance()
        if (currentBalance < DEFAULT_CREDIT_LIMIT) {
            val beyondLimitEvent = SessionScheduledBeyondLimit(
                studentId = command.studentId,
                sessionId = command.sessionId
            )
            eventAppender.append(beyondLimitEvent)
        }

        return DebtAcknowledgmentResult(success = true)
    }
}

