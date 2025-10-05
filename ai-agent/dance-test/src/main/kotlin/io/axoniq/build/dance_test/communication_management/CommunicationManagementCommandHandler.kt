package io.axoniq.build.dance_test.communication_management

import io.axoniq.build.dance_test.communication_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * Command handler for Communication Management component.
 * Handles payment reminders and instructor-student communication including
 * financial record exports, student waiting list creation, and payment reminder sending.
 */
class CommunicationManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CommunicationManagementCommandHandler::class.java)
    }

    /**
     * Handles ExportFinancialRecords command.
     * Exports financial records for the specified date range and format.
     */
    @CommandHandler
    fun handle(
        command: ExportFinancialRecords,
        @InjectEntity state: CommunicationManagementState,
        eventAppender: EventAppender
    ): FinancialExportResult {
        logger.info("Handling ExportFinancialRecords command for instructor: ${command.instructorId}")
        
        val event = FinancialRecordsExported(
            startDate = command.startDate,
            endDate = command.endDate,
            exportFormat = command.exportFormat,
            exportDate = LocalDateTime.now(),
            instructorId = command.instructorId
        )

        eventAppender.append(event)
        logger.info("Financial records exported for instructor: ${command.instructorId}")

        return FinancialExportResult(
            success = true,
            exportFileUrl = "/exports/financial_${command.instructorId}_${LocalDateTime.now().toLocalDate()}.${command.exportFormat.lowercase()}"
        )
    }

    /**
     * Handles CreateStudentWaitingList command.
     * Creates a waiting list for students under the specified instructor.
     */
    @CommandHandler
    fun handle(
        command: CreateStudentWaitingList,
        @InjectEntity state: CommunicationManagementState,
        eventAppender: EventAppender
    ): WaitingListResult {
        logger.info("Handling CreateStudentWaitingList command for instructor: ${command.instructorId}")

        if (state.getWaitingListExists()) {
            logger.warn("Waiting list already exists for instructor: ${command.instructorId}")
            return WaitingListResult(success = false)
        }
        
        val event = WaitingListCreated(
            creationDate = LocalDateTime.now(),
            instructorId = command.instructorId
        )
        
        eventAppender.append(event)
        logger.info("Waiting list created for instructor: ${command.instructorId}")
        
        return WaitingListResult(success = true)
    }

    /**
     * Handles SendPaymentReminder command.
     * Sends payment reminder to the specified student from the instructor.
     */
    @CommandHandler
    fun handle(
        command: SendPaymentReminder,
        @InjectEntity state: CommunicationManagementState,
        eventAppender: EventAppender
    ): PaymentReminderResult {
        logger.info("Handling SendPaymentReminder command for student: ${command.studentId} from instructor: ${command.instructorId}")

        val event = PaymentReminderSent(
            instructorId = command.instructorId,
            sentDate = LocalDateTime.now(),
            studentId = command.studentId,
            reminderType = command.reminderType
        )

        eventAppender.append(event)
        logger.info("Payment reminder sent to student: ${command.studentId}")

        return PaymentReminderResult(success = true)
    }
}

