package io.axoniq.build.jupiter_wheels.bluetooth_system

import io.axoniq.build.jupiter_wheels.bluetooth_system.api.PaymentCompleted
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Bluetooth System Integration - External System Component
 *
 * Handles bike unlocking through Bluetooth communication when payment is completed.
 * This component acts as an integration point between the Jupiter Wheels system
 * and the external Bluetooth-enabled bike locking mechanism.
 */
@Service
class BluetoothSystemIntegration(
    private val commandGateway: CommandGateway
) {
    
    private val logger: Logger = LoggerFactory.getLogger(BluetoothSystemIntegration::class.java)

    /**
     * Handles PaymentCompleted events to trigger bike unlocking via Bluetooth.
     * 
     * When a payment is completed for a rental, this handler initiates the Bluetooth
     * communication to unlock the corresponding bike. This is a stub implementation
     * that logs the intended action.
     * 
     * @param event The PaymentCompleted event containing payment and rental information
     * @param processingContext The processing context for command execution
     */
    @EventHandler
    fun handle(event: PaymentCompleted, processingContext: ProcessingContext) {
        logger.info("Bluetooth System: Unlocking bike for rental ${event.rentalId} after payment ${event.paymentId} completion")

        // TODO: Implement actual Bluetooth communication to unlock the bike
        // This would typically involve:
        // 1. Retrieving bike location and Bluetooth device information
        // 2. Establishing Bluetooth connection with the bike's locking mechanism
        // 3. Sending unlock command via Bluetooth protocol
        // 4. Handling connection failures and retries

        logger.debug("Bluetooth unlock command would be sent for rental: ${event.rentalId}")

        // Note: In a real implementation, this would send a command back to the system
        // to confirm the bike unlock status, but no specific command is defined in the requirements
    }
}