package io.axoniq.build.dance_test.session_booking

import io.axoniq.build.dance_test.session_booking.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Session Booking component that exposes endpoints for
 * session scheduling, cancellation and negative balance booking operations.
 */
@RestController
@RequestMapping("/api/session-booking")
class SessionBookingController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SessionBookingController::class.java)
    }

    @PostMapping("/create")
    fun createSessionBooking(@RequestBody command: CreateSessionBooking): ResponseEntity<String> {
        logger.info("Dispatching CreateSessionBooking command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Session booking created successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateSessionBooking command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create session booking")
        }
    }

    @PostMapping("/create-negative-balance")
    fun createNegativeBalanceSession(@RequestBody command: CreateNegativeBalanceSession): ResponseEntity<String> {
        logger.info("Dispatching CreateNegativeBalanceSession command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Negative balance session created successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateNegativeBalanceSession command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create negative balance session")
        }
    }

    @PostMapping("/cancel")
    fun cancelSessionBooking(@RequestBody command: CancelSessionBooking): ResponseEntity<String> {
        logger.info("Dispatching CancelSessionBooking command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Session booking cancelled successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CancelSessionBooking command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to cancel session booking")
        }
    }

    @PostMapping("/decide-cancellation-charges")
    fun decideCancellationCharges(@RequestBody command: DecideCancellationCharges): ResponseEntity<String> {
        logger.info("Dispatching DecideCancellationCharges command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Cancellation decision recorded successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch DecideCancellationCharges command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to record cancellation decision")
        }
    }

    @PostMapping("/acknowledge-debt")
    fun acknowledgeDebtAccumulation(@RequestBody command: AcknowledgeDebtAccumulation): ResponseEntity<String> {
        logger.info("Dispatching AcknowledgeDebtAccumulation command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Debt acknowledgment recorded successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch AcknowledgeDebtAccumulation command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to record debt acknowledgment")
        }
    }
}

