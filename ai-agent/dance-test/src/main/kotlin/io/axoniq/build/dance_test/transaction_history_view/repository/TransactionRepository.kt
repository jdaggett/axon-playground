package io.axoniq.build.dance_test.transaction_history_view.repository

import io.axoniq.build.dance_test.transaction_history_view.entity.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * JPA repository for TransactionEntity in the Transaction History View component.
 * Provides database operations for querying transaction history data.
 */
@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, String> {

    /**
     * Find transactions by student ID within a date range, optionally filtered by transaction type.
     */
    fun findByStudentIdAndTransactionDateBetweenAndTransactionTypeOrderByTransactionDateDesc(
        studentId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        transactionType: String
    ): List<TransactionEntity>

    /**
     * Find transactions by student ID within a date range.
     */
    fun findByStudentIdAndTransactionDateBetweenOrderByTransactionDateDesc(
        studentId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<TransactionEntity>

    /**
     * Find transactions by student ID, optionally filtered by transaction type.
     */
    fun findByStudentIdAndTransactionTypeOrderByTransactionDateDesc(
        studentId: String,
        transactionType: String
    ): List<TransactionEntity>
    
    /**
     * Find all transactions by student ID.
     */
    fun findByStudentIdOrderByTransactionDateDesc(studentId: String): List<TransactionEntity>
}

