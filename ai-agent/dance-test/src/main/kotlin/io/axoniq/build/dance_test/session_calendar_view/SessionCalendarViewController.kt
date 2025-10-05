package io.axoniq.build.dance_test.session_calendar_view

import io.axoniq.build.dance_test.session_calendar_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

/**
 * REST controller for Session Calendar View - Provides HTTP endpoints for session calendar and detail queries.
 * Exposes the read model through REST API endpoints for frontend consumption.
 */
@RestController
@RequestMapping("/api/session-calendar")
class SessionCalendarViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SessionCalendarViewController::class.java)
    }

    /**
     * Get detailed information about a specific session.
     */
    @GetMapping("/sessions/{sessionId}")
    fun getSessionDetails(@PathVariable sessionId: String): CompletableFuture<SessionDetailsData> {
        logger.info("REST request for session details: $sessionId")

        val query = SessionDetails(sessionId = sessionId)
        return queryGateway.query(query, SessionDetailsData::class.java, null)
    }

    /**
     * Get calendar view for an instructor within a specified date range.
     */
    @GetMapping("/instructors/{instructorId}/calendar")
    fun getInstructorCalendar(
        @PathVariable instructorId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): CompletableFuture<InstructorCalendarData> {
        logger.info("REST request for instructor calendar: $instructorId from $startDate to $endDate")

        val query = InstructorCalendar(
            instructorId = instructorId,
            startDate = startDate,
            endDate = endDate
        )
        return queryGateway.query(query, InstructorCalendarData::class.java, null)
    }
}