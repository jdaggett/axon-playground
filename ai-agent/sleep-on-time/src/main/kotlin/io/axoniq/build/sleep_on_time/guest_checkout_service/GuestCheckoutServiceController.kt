package io.axoniq.build.sleep_on_time.guest_checkout_service

import io.axoniq.build.sleep_on_time.guest_checkout_service.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Guest Checkout Service component.
 * Exposes endpoints for guest checkout operations.
 */
@RestController
@RequestMapping("/api/guest-checkout")
class GuestCheckoutServiceController(
    private val commandGateway: CommandGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GuestCheckoutServiceController::class.java)
    }

    /**
     * Initiates the guest checkout process.
     * 
     * @param request The checkout initiation request containing booking, guest, and container information
     * @return ResponseEntity indicating the result of the checkout initiation
     */
    @PostMapping("/initiate")
    fun initiateCheckout(@RequestBody request: InitiateCheckOutRequest): ResponseEntity<String> {
        val command = InitiateCheckOut(
            bookingId = request.bookingId,
            guestId = request.guestId,
            containerId = request.containerId
        )

        logger.info("Dispatching InitiateCheckOut command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Checkout initiated successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch InitiateCheckOut command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to initiate checkout")
        }
    }
}

/**
 * Request DTO for initiating guest checkout.
 */
data class InitiateCheckOutRequest(
    val bookingId: String,
    val guestId: String,
    val containerId: String
)

