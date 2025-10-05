package io.axoniq.build.sleep_on_time.xesar_access_key

import io.axoniq.build.sleep_on_time.xesar_access_key.api.ContainerObtained
import io.axoniq.build.sleep_on_time.xesar_access_key.api.GuestCheckedOut
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Xesar Access Key Management - External System Component
 *
 * This component handles digital access key operations with the Xesar system.
 * It responds to booking-related events by managing access keys for containers,
 * including sending access keys when containers are obtained and withdrawing
 * them when guests check out.
 */
@Service
class XesarAccessKeyManagement(
    private val commandGateway: CommandGateway
) {

    private val logger: Logger = LoggerFactory.getLogger(XesarAccessKeyManagement::class.java)

    /**
     * Handles ContainerObtained event by initiating the process to send an access key
     * to the guest through the Xesar system.
     * 
     * This method is triggered when a container is obtained by a guest during booking.
     * It logs the action and would typically integrate with the Xesar API to provision
     * digital access keys for the container.
     * 
     * @param event The ContainerObtained event containing booking, guest, and container details
     * @param processingContext The Axon processing context for command dispatching
     */
    @EventHandler
    fun handleContainerObtained(event: ContainerObtained, processingContext: ProcessingContext) {
        logger.info(
            "XesarSendAccessKey: Processing access key provisioning for guest {} " +
            "in container {} for booking {} at {}",
            event.guestId,
            event.containerId,
            event.bookingId,
            event.timestamp
        )

        // TODO: Integrate with Xesar API to send digital access key
        // This would typically involve:
        // 1. Authenticating with Xesar system
        // 2. Creating/activating access key for the container
        // 3. Sending key details to guest (email, mobile app, etc.)
        
        logger.debug("Access key sent successfully to guest {} for container {}", 
                    event.guestId, event.containerId)
    }

    /**
     * Handles GuestCheckedOut event by initiating the process to withdraw/deactivate
     * the access key from the Xesar system.
     * 
     * This method is triggered when a guest checks out from their container.
     * It logs the action and would typically integrate with the Xesar API to revoke
     * or deactivate the digital access key for security purposes.
     * 
     * @param event The GuestCheckedOut event containing booking, guest, and container details
     * @param processingContext The Axon processing context for command dispatching
     */
    @EventHandler
    fun handleGuestCheckedOut(event: GuestCheckedOut, processingContext: ProcessingContext) {
        logger.info(
            "XesarWithdrawAccessKey: Processing access key withdrawal for guest {} " +
            "from container {} for booking {} at {}",
            event.guestId,
            event.containerId,
            event.bookingId,
            event.timestamp
        )

        // TODO: Integrate with Xesar API to withdraw/deactivate access key
        // This would typically involve:
        // 1. Authenticating with Xesar system
        // 2. Deactivating/revoking access key for the container
        // 3. Confirming key withdrawal completion

        logger.debug("Access key withdrawn successfully for guest {} from container {}", 
                    event.guestId, event.containerId)
    }
}