package io.axoniq.build.dance_test.payment_management

import io.axoniq.build.dance_test.payment_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Payment Management component.
 * Exposes endpoints for payment recording and balance adjustments.
 */
@RestController
@RequestMapping("/api/payment-management")
class PaymentManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PaymentManagementController::class.java)
    }

    /**
     * Records a student payment.
     * 
     * @param command The RecordStudentPayment command
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/record-payment")
    fun recordStudentPayment(@RequestBody command: RecordStudentPayment): ResponseEntity<String> {
        logger.info("Dispatching RecordStudentPayment command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Payment recording accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RecordStudentPayment command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to record student payment")
        }
    }

    /**
     * Adjusts a student's balance.
     * 
     * @param command The AdjustStudentBalance command
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/adjust-balance")
    fun adjustStudentBalance(@RequestBody command: AdjustStudentBalance): ResponseEntity<String> {
        logger.info("Dispatching AdjustStudentBalance command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Balance adjustment accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch AdjustStudentBalance command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to adjust student balance")
        }
    }
}

