package io.axoniq.build.dance_test.payment_management

import io.axoniq.build.dance_test.payment_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal

/**
 * Command handler for the Payment Management component.
 * Handles student payment recording and balance adjustments.
 */
class PaymentManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PaymentManagementCommandHandler::class.java)
    }

    /**
     * Handles the RecordStudentPayment command by recording the payment and updating the student's monetary balance.
     * 
     * @param command The RecordStudentPayment command containing payment details
     * @param state The current payment management state for the student
     * @param eventAppender Used to append events to the event stream
     * @return PaymentResult indicating success and the new balance
     */
    @CommandHandler
    fun handle(
        command: RecordStudentPayment,
        @InjectEntity state: PaymentManagementState,
        eventAppender: EventAppender
    ): PaymentResult {
        logger.info("Recording student payment for studentId: ${command.studentId}, amount: ${command.amount}")

        // Record the payment event
        eventAppender.append(
            PaymentRecorded(
                amount = command.amount,
                paymentMethod = command.paymentMethod,
                paymentDate = command.paymentDate,
                studentId = command.studentId
            )
        )

        // Increase the monetary balance from payment
        eventAppender.append(
            MonetaryBalanceIncreasedFromPayment(
                amount = command.amount,
                studentId = command.studentId
            )
        )

        val newBalance = state.getMonetaryBalance().add(BigDecimal.valueOf(command.amount))

        logger.info("Payment recorded successfully for studentId: ${command.studentId}, new balance: $newBalance")
        return PaymentResult(success = true, newBalance = newBalance.toDouble())
    }

    /**
     * Handles the AdjustStudentBalance command by recording the balance adjustment.
     * 
     * @param command The AdjustStudentBalance command containing adjustment details
     * @param state The current payment management state for the student
     * @param eventAppender Used to append events to the event stream
     * @return BalanceAdjustmentResult indicating success and the new balance
     */
    @CommandHandler
    fun handle(
        command: AdjustStudentBalance,
        @InjectEntity state: PaymentManagementState,
        eventAppender: EventAppender
    ): BalanceAdjustmentResult {
        logger.info("Adjusting student balance for studentId: ${command.studentId}, adjustment: ${command.adjustmentAmount}")

        // Record the balance adjustment
        eventAppender.append(
            BalanceAdjustmentRecorded(
                adjustmentAmount = command.adjustmentAmount,
                studentId = command.studentId,
                adjustmentReason = command.adjustmentReason
            )
        )

        val newBalance = state.getMonetaryBalance().add(BigDecimal.valueOf(command.adjustmentAmount))

        logger.info("Balance adjustment recorded for studentId: ${command.studentId}, new balance: $newBalance")
        return BalanceAdjustmentResult(success = true, newBalance = newBalance.toDouble())
    }
}