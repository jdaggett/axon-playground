package io.axoniq.build.dance_test.transaction_history_view.repository

import io.axoniq.build.dance_test.transaction_history_view.entity.SessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * JPA repository for SessionEntity in the Transaction History View component.
 * Provides database operations for querying session history data.
 */
@Repository
interface SessionRepository : JpaRepository<SessionEntity, String> {

    /**
     * Find sessions by student ID within a date range.
     */
    fun findByStudentIdAndSessionDateBetweenOrderBySessionDateDesc(
        studentId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<SessionEntity>

    /**
     * Find all sessions by student ID.
     */
    fun findByStudentIdOrderBySessionDateDesc(studentId: String): List<SessionEntity>
}

