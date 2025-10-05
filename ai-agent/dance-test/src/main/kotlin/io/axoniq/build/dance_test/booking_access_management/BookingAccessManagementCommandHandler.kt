package io.axoniq.build.dance_test.booking_access_management

import io.axoniq.build.dance_test.booking_access_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * BookingAccessManagementCommandHandler - Handles commands for booking access management
 * Manages student booking access blocking, unblocking and balance decisions
 */
class BookingAccessManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BookingAccessManagementCommandHandler::class.java)
    }

    /**
     * Handles BlockStudentBookingAccess command - blocks booking access for a student
     */
    @CommandHandler
    fun handle(
        command: BlockStudentBookingAccess,
        @InjectEntity state: BookingAccessManagementState,
        eventAppender: EventAppender): BookingBlockResult {
        logger.info("Handling BlockStudentBookingAccess command for student: ${command.studentId}")

        val event = BookingAccessBlocked(
            instructorId = command.instructorId,
            blockingReason = command.blockingReason,
            studentId = command.studentId
        )

        eventAppender.append(event)
        logger.info("BookingAccessBlocked event appended for student: ${command.studentId}")

        return BookingBlockResult(success = true)
    }

    /**
     * Handles UpdateBookingAccess command - updates booking access status for a student
     */
    @CommandHandler
    fun handle(
        command: UpdateBookingAccess,
        @InjectEntity state: BookingAccessManagementState,
        eventAppender: EventAppender
    ): BookingAccessResult {
        logger.info("Handling UpdateBookingAccess command for student: ${command.studentId}")

        val event = BookingAccessStatusUpdated(
            instructorId = command.instructorId,
            newAccessStatus = command.newAccessStatus,
            reason = command.reason,
            studentId = command.studentId
        )

        eventAppender.append(event)
        logger.info("BookingAccessStatusUpdated event appended for student: ${command.studentId}")

        return BookingAccessResult(success = true)
    }

    /**
     * Handles HandleBlockingWithBalances command - records balance preservation decision during blocking
     */
    @CommandHandler
    fun handle(
        command: HandleBlockingWithBalances,
        @InjectEntity state: BookingAccessManagementState,
        eventAppender: EventAppender
    ): BlockingBalanceResult {
        logger.info("Handling HandleBlockingWithBalances command for student: ${command.studentId}")

        val event = BlockingBalanceDecisionRecorded(
            instructorId = command.instructorId,
            preserveBalances = command.preserveBalances,
            studentId = command.studentId
        )

        eventAppender.append(event)
        logger.info("BlockingBalanceDecisionRecorded event appended for student: ${command.studentId}")

        return BlockingBalanceResult(success = true)
    }
}

