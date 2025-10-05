package io.axoniq.build.dance_test.transaction_history_view

import io.axoniq.build.dance_test.transaction_history_view.api.*
import io.axoniq.build.dance_test.transaction_history_view.entity.SessionEntity
import io.axoniq.build.dance_test.transaction_history_view.entity.TransactionEntity
import io.axoniq.build.dance_test.transaction_history_view.repository.SessionRepository
import io.axoniq.build.dance_test.transaction_history_view.repository.TransactionRepository
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Transaction History View component that handles transaction and session history queries.
 * Maintains read models for financial transactions and student session data.
 */
@Component
class TransactionHistoryViewComponent(
    private val transactionRepository: TransactionRepository,
    private val sessionRepository: SessionRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TransactionHistoryViewComponent::class.java)
    }

    /**
     * Query handler for retrieving transaction history based on student ID, date range, and transaction type.
     * Returns a list of transactions matching the specified criteria.
     */
    @QueryHandler
    fun handle(query: TransactionHistory): TransactionHistoryData {
        logger.info("Handling TransactionHistory query for student: ${query.studentId}")
        
        val transactions = when {
            query.startDate != null && query.endDate != null && query.transactionType != null -> {
                transactionRepository.findByStudentIdAndTransactionDateBetweenAndTransactionTypeOrderByTransactionDateDesc(
                    query.studentId,
                    query.startDate,
                    query.endDate,
                    query.transactionType
                )
            }
            query.startDate != null && query.endDate != null -> {
                transactionRepository.findByStudentIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                    query.studentId,
                    query.startDate,
                    query.endDate
                )
            }
            query.transactionType != null -> {
                transactionRepository.findByStudentIdAndTransactionTypeOrderByTransactionDateDesc(
                    query.studentId,
                    query.transactionType
                )
            }
            else -> {
                transactionRepository.findByStudentIdOrderByTransactionDateDesc(query.studentId)
            }
        }

        val transactionItems = transactions.map { entity ->
            TransactionItem(
                transactionId = entity.transactionId,
                amount = entity.amount,
                description = entity.description,
                transactionDate = entity.transactionDate,
                transactionType = entity.transactionType
            )
        }

        return TransactionHistoryData(
            transactions = transactionItems,
            totalTransactions = transactionItems.size
        )
    }

    /**
     * Query handler for retrieving student session history based on student ID and date range.
     * Returns a list of sessions matching the specified criteria.
     */
    @QueryHandler
    fun handle(query: StudentSessionHistory): StudentSessionHistoryData {
        logger.info("Handling StudentSessionHistory query for student: ${query.studentId}")
        
        val sessions = when {
            query.startDate != null && query.endDate != null -> {
                sessionRepository.findByStudentIdAndSessionDateBetweenOrderBySessionDateDesc(
                    query.studentId,
                    query.startDate.atStartOfDay(),
                    query.endDate.atTime(23, 59, 59)
                )
            }
            else -> {
                sessionRepository.findByStudentIdOrderBySessionDateDesc(query.studentId)
            }
        }

        val sessionItems = sessions.map { entity ->
            SessionHistoryItem(
                sessionId = entity.sessionId,
                sessionDate = entity.sessionDate,
                duration = entity.duration,
                actualDuration = entity.actualDuration,
                status = entity.status,
                notes = entity.notes
            )
        }

        return StudentSessionHistoryData(
            sessions = sessionItems,
            totalSessions = sessionItems.size
        )
    }

    /**
     * Event handler for TransactionRecordCreated events.
     * Creates a new transaction record in the read model.
     */
    @EventHandler
    fun on(event: TransactionRecordCreated) {
        logger.info("Handling TransactionRecordCreated event for student: ${event.studentId}")

        val transaction = TransactionEntity(
            transactionId = UUID.randomUUID().toString(),
            studentId = event.studentId,
            transactionType = event.transactionType,
            amount = event.amount,
            description = event.description,
            transactionDate = LocalDate.now()
        )

        transactionRepository.save(transaction)
    }

    /**
     * Event handler for PaymentRecorded events.
     * Creates a payment transaction record in the read model.
     */
    @EventHandler
    fun on(event: PaymentRecorded) {
        logger.info("Handling PaymentRecorded event for student: ${event.studentId}")

        val transaction = TransactionEntity(
            transactionId = UUID.randomUUID().toString(),
            studentId = event.studentId,
            transactionType = "PAYMENT",
            amount = event.amount,
            description = "Payment via ${event.paymentMethod}",
            transactionDate = event.paymentDate
        )

        transactionRepository.save(transaction)
    }

    /**
     * Event handler for BalanceAdjustmentRecorded events.
     * Creates a balance adjustment transaction record in the read model.
     */
    @EventHandler
    fun on(event: BalanceAdjustmentRecorded) {
        logger.info("Handling BalanceAdjustmentRecorded event for student: ${event.studentId}")

        val transaction = TransactionEntity(
            transactionId = UUID.randomUUID().toString(),
            studentId = event.studentId,
            transactionType = "ADJUSTMENT",
            amount = event.adjustmentAmount,
            description = event.adjustmentReason,
            transactionDate = LocalDate.now()
        )

        transactionRepository.save(transaction)
    }

    /**
     * Event handler for SessionScheduled events.
     * Creates a new session record in the read model.
     */
    @EventHandler
    fun on(event: SessionScheduled) {
        logger.info("Handling SessionScheduled event for session: ${event.sessionId}")

        val session = SessionEntity(
            sessionId = event.sessionId,
            studentId = event.studentId,
            instructorId = event.instructorId,
            sessionDate = event.sessionDate,
            duration = event.duration,
            actualDuration = null,
            status = "SCHEDULED",
            notes = null
        )

        sessionRepository.save(session)
    }

    /**
     * Event handler for SessionCompleted events.
     * Updates session record with completion data.
     */
    @EventHandler
    fun on(event: SessionCompleted) {
        logger.info("Handling SessionCompleted event for session: ${event.sessionId}")

        sessionRepository.findById(event.sessionId).ifPresent { session ->
            val updatedSession = session.copy(
                status = "COMPLETED",
                actualDuration = event.actualDuration
            )
            sessionRepository.save(updatedSession)
        }
    }

    /**
     * Event handler for SessionCancelled events.
     * Updates session record with cancellation status.
     */
    @EventHandler
    fun on(event: SessionCancelled) {
        logger.info("Handling SessionCancelled event for session: ${event.sessionId}")

        sessionRepository.findById(event.sessionId).ifPresent { session ->
            val updatedSession = session.copy(
                status = "CANCELLED",
                notes = "Cancelled at ${event.cancellationTime}"
            )
            sessionRepository.save(updatedSession)
        }
    }

    /**
     * Event handler for SessionMarkedAsNoShow events.
     * Updates session record with no-show status and reason.
     */
    @EventHandler
    fun on(event: SessionMarkedAsNoShow) {
        logger.info("Handling SessionMarkedAsNoShow event for session: ${event.sessionId}")

        sessionRepository.findById(event.sessionId).ifPresent { session ->
            val updatedSession = session.copy(
                status = "NO_SHOW",
                notes = event.reason
            )
            sessionRepository.save(updatedSession)
        }
    }
}

