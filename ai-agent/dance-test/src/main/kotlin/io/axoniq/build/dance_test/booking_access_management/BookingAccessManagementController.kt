package io.axoniq.build.dance_test.booking_access_management

import io.axoniq.build.dance_test.booking_access_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * BookingAccessManagementController - REST controller for booking access management
 * Exposes endpoints for blocking access, updating access status, and handling balance decisions
 */
@RestController
@RequestMapping("/api/booking-access")
class BookingAccessManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BookingAccessManagementController::class.java)
    }

    /**
     * Blocks booking access for a student
     */
    @PostMapping("/block")
    fun blockStudentBookingAccess(@RequestBody command: BlockStudentBookingAccess): ResponseEntity<String> {
        logger.info("Dispatching BlockStudentBookingAccess command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Booking access blocked successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch BlockStudentBookingAccess command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to block booking access")
        }
    }

    /**
     * Updates booking access status for a student
     */
    @PutMapping("/update")
    fun updateBookingAccess(@RequestBody command: UpdateBookingAccess): ResponseEntity<String> {
        logger.info("Dispatching UpdateBookingAccess command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Booking access updated successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch UpdateBookingAccess command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update booking access")
        }
    }

    /**
     * Handles balance preservation decision during blocking
     */
    @PostMapping("/handle-balances")
    fun handleBlockingWithBalances(@RequestBody command: HandleBlockingWithBalances): ResponseEntity<String> {
        logger.info("Dispatching HandleBlockingWithBalances command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Balance handling decision recorded successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch HandleBlockingWithBalances command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to handle balance decision")
        }
    }
}

