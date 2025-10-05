package io.axoniq.build.sleep_on_time.guest_checkout_service

import io.axoniq.build.sleep_on_time.guest_checkout_service.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * Command handler for the Guest Checkout Service component.
 * Handles guest checkout process by processing InitiateCheckOut commands
 * and emitting appropriate events based on the current state.
 */
class GuestCheckoutServiceCommandHandler {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GuestCheckoutServiceCommandHandler::class.java)
    }

    /**
     * Handles the InitiateCheckOut command for the Guest Checkout Service component.
     * Validates that the guest is checked in and initiates the checkout process.
     *
     * @param command The InitiateCheckOut command containing booking, guest, and container information
     * @param state The current state of the guest checkout process
     * @param eventAppender Used to append events to the event store
     * @return CheckOutResult indicating success status and access key withdrawal status
     */
    @CommandHandler
    fun handle(
        command: InitiateCheckOut,
        @InjectEntity state: GuestCheckoutServiceState,
        eventAppender: EventAppender
    ): CheckOutResult {
        logger.info("Handling InitiateCheckOut command for booking: ${command.bookingId}, guest: ${command.guestId}")

        // Validate that the guest is currently checked in
        if (!state.getCheckedInStatus()) {
            logger.warn("Guest ${command.guestId} is not checked in, cannot initiate checkout")
            return CheckOutResult(success = false, accessKeyWithdrawn = false)
        }

        // Create and append the GuestCheckedOut event
        val checkoutEvent = GuestCheckedOut(
            bookingId = command.bookingId,
            guestId = command.guestId,
            timestamp = LocalDateTime.now(),
            containerId = command.containerId
        )
        
        eventAppender.append(checkoutEvent)

        logger.info("Guest checkout initiated successfully for guest: ${command.guestId}")
        return CheckOutResult(success = true, accessKeyWithdrawn = true)
    }
}

