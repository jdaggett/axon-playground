package io.axoniq.build.dance_test.transaction_history_view.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * JPA entity for storing session history data in the Transaction History View component.
 * Stores information about dance sessions including scheduling, completion, and cancellation data.
 */
@Entity
@Table(name = "sessions")
data class SessionEntity(
    @Id
    @Column(name = "session_id")
    val sessionId: String,
    
    @Column(name = "student_id", nullable = false)
    val studentId: String,

    @Column(name = "instructor_id", nullable = false)
    val instructorId: String,

    @Column(name = "session_date", nullable = false)
    val sessionDate: LocalDateTime,

    @Column(name = "duration", nullable = false)
    val duration: Int,

    @Column(name = "actual_duration")
    val actualDuration: Int?,

    @Column(name = "status", nullable = false)
    val status: String,

    @Column(name = "notes")
    val notes: String?
) {
    constructor() : this("", "", "", LocalDateTime.now(), 0, null, "SCHEDULED", null)
}

