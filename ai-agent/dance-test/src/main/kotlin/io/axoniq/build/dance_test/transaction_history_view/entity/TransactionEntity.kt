package io.axoniq.build.dance_test.transaction_history_view.entity

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA entity for storing transaction history data in the Transaction History View component.
 * Stores all financial transactions including payments, charges, and balance adjustments.
 */
@Entity
@Table(name = "transactions")
data class TransactionEntity(
    @Id
    @Column(name = "transaction_id")
    val transactionId: String,

    @Column(name = "student_id", nullable = false)
    val studentId: String,

    @Column(name = "transaction_type", nullable = false)
    val transactionType: String,

    @Column(name = "amount", nullable = false)
    val amount: Double,

    @Column(name = "description", nullable = false)
    val description: String,
    
    @Column(name = "transaction_date", nullable = false)
    val transactionDate: LocalDate
) {
    constructor() : this("", "", "", 0.0, "", LocalDate.now())
}

