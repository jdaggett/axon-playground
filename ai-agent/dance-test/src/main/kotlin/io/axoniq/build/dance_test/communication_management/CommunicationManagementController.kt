package io.axoniq.build.dance_test.communication_management

import io.axoniq.build.dance_test.communication_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Communication Management component.
 * Exposes endpoints for handling payment reminders and instructor-student communication.
 */
@RestController
@RequestMapping("/api/communication-management")
class CommunicationManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CommunicationManagementController::class.java)
    }

    /**
     * Exports financial records for the specified instructor and date range.
     */
    @PostMapping("/export-financial-records")
    fun exportFinancialRecords(@RequestBody command: ExportFinancialRecords): ResponseEntity<String> {
        logger.info("Dispatching ExportFinancialRecords command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Financial records export accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ExportFinancialRecords command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to export financial records")
        }
    }

    /**
     * Creates a student waiting list for the specified instructor.
     */
    @PostMapping("/create-waiting-list")
    fun createStudentWaitingList(@RequestBody command: CreateStudentWaitingList): ResponseEntity<String> {
        logger.info("Dispatching CreateStudentWaitingList command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Waiting list creation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateStudentWaitingList command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create waiting list")
        }
    }

    /**
     * Sends a payment reminder to the specified student.
     */
    @PostMapping("/send-payment-reminder")
    fun sendPaymentReminder(@RequestBody command: SendPaymentReminder): ResponseEntity<String> {
        logger.info("Dispatching SendPaymentReminder command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Payment reminder accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch SendPaymentReminder command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send payment reminder")
        }
    }
}

