package io.axoniq.build.dance_test.transaction_history_view

import io.axoniq.build.dance_test.transaction_history_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the Transaction History View component.
 * Exposes endpoints for querying transaction and session history data.
 */
@RestController
@RequestMapping("/api/transaction-history")
class TransactionHistoryViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TransactionHistoryViewController::class.java)
    }

    /**
     * GET endpoint for retrieving transaction history for a specific student.
     * Supports optional query parameters for date range and transaction type filtering.
     */
    @GetMapping("/transactions/{studentId}")
    fun getTransactionHistory(
        @PathVariable studentId: String,
        @RequestParam(required = false) transactionType: String?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?
    ): CompletableFuture<TransactionHistoryData> {
        logger.info("REST request for transaction history - Student: $studentId, Type: $transactionType, Start: $startDate, End: $endDate")
        
        val query = TransactionHistory(
            studentId = studentId,
            transactionType = transactionType,
            startDate = startDate,
            endDate = endDate
        )
        
        return queryGateway.query(query, TransactionHistoryData::class.java, null)
    }

    /**
     * GET endpoint for retrieving session history for a specific student.
     * Supports optional query parameters for date range filtering.
     */
    @GetMapping("/sessions/{studentId}")
    fun getStudentSessionHistory(
        @PathVariable studentId: String,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?
    ): CompletableFuture<StudentSessionHistoryData> {
        logger.info("REST request for session history - Student: $studentId, Start: $startDate, End: $endDate")

        val query = StudentSessionHistory(
            studentId = studentId,
            startDate = startDate,
            endDate = endDate
        )

        return queryGateway.query(query, StudentSessionHistoryData::class.java, null)
    }
}