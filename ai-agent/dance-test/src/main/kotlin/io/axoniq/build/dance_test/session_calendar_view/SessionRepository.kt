package io.axoniq.build.dance_test.session_calendar_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Repository interface for managing SessionEntity data in the Session Calendar View component.
 * Provides data access methods for session queries and calendar operations.
 */
interface SessionRepository : JpaRepository<SessionEntity, String> {

    /**
     * Finds sessions for a specific instructor within a date range for calendar display.
     */
    @Query("SELECT s FROM SessionEntity s WHERE s.instructorId = :instructorId AND DATE(s.sessionDate) BETWEEN :startDate AND :endDate ORDER BY s.sessionDate")
    fun findByInstructorIdAndDateRange(
        @Param("instructorId") instructorId: String,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<SessionEntity>
}

