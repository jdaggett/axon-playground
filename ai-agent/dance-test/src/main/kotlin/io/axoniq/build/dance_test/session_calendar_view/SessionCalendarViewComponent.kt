package io.axoniq.build.dance_test.session_calendar_view

import io.axoniq.build.dance_test.session_calendar_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Session Calendar View Component - Provides instructor calendar and session scheduling views.
 * This query component handles session-related events and provides calendar and session detail queries.
 */
@Component
class SessionCalendarViewComponent(
    private val sessionRepository: SessionRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SessionCalendarViewComponent::class.java)
    }

    /**
     * Handles SessionDetails query to retrieve detailed information about a specific session.
     */
    @QueryHandler
    fun handle(query: SessionDetails): SessionDetailsData {
        logger.info("Handling SessionDetails query for sessionId: ${query.sessionId}")

        val session = sessionRepository.findById(query.sessionId)
            .orElseThrow { IllegalArgumentException("Session not found: ${query.sessionId}") }

        return SessionDetailsData(
            duration = session.duration,
            sessionDate = session.sessionDate,
            studentId = session.studentId,
            notes = session.notes,
            status = session.status,
            sessionId = session.sessionId,
            studentName = session.studentName,
            actualDuration = session.actualDuration
        )
    }

    /**
     * Handles InstructorCalendar query to retrieve calendar sessions for an instructor within a date range.
     */
    @QueryHandler
    fun handle(query: InstructorCalendar): InstructorCalendarData {
        logger.info("Handling InstructorCalendar query for instructorId: ${query.instructorId}, from: ${query.startDate}, to: ${query.endDate}")

        val sessions = sessionRepository.findByInstructorIdAndDateRange(
            query.instructorId,
            query.startDate,
            query.endDate
        )
        
        val calendarSessions = sessions.map { session ->
            CalendarSession(
                duration = session.duration,
                sessionDate = session.sessionDate,
                status = session.status,
                sessionId = session.sessionId,
                studentName = session.studentName
            )
        }

        return InstructorCalendarData(sessions = calendarSessions)
    }

    /**
     * Handles SessionScheduled event to create a new session entry in the view.
     */
    @EventHandler
    @Transactional
    fun on(event: SessionScheduled) {
        logger.info("Handling SessionScheduled event for sessionId: ${event.sessionId}")

        // Note: We need to get student name from somewhere - for now using placeholder
        // In a real system, this might require a lookup to another service
        val studentName = "Student ${event.studentId}" // Placeholder implementation

        val session = SessionEntity(
            sessionId = event.sessionId,
            instructorId = event.instructorId,
            duration = event.duration,
            sessionDate = event.sessionDate,
            studentId = event.studentId,
            notes = null,
            status = "SCHEDULED",
            studentName = studentName,
            actualDuration = null
        )

        sessionRepository.save(session)
    }

    /**
     * Handles SessionCompleted event to update session status and actual duration.
     */
    @EventHandler
    @Transactional
    fun on(event: SessionCompleted) {
        logger.info("Handling SessionCompleted event for sessionId: ${event.sessionId}")

        val session = sessionRepository.findById(event.sessionId)
            .orElseThrow { IllegalArgumentException("Session not found: ${event.sessionId}") }
        
        val updatedSession = session.copy(
            status = "COMPLETED",
            actualDuration = event.actualDuration
        )

        sessionRepository.save(updatedSession)
    }

    /**
     * Handles SessionDetailsModified event to update session details like duration and date.
     */
    @EventHandler
    @Transactional
    fun on(event: SessionDetailsModified) {
        logger.info("Handling SessionDetailsModified event for sessionId: ${event.sessionId}")

        val session = sessionRepository.findById(event.sessionId)
            .orElseThrow { IllegalArgumentException("Session not found: ${event.sessionId}") }

        val updatedSession = session.copy(
            duration = event.newDuration ?: session.duration,
            sessionDate = event.newSessionDate ?: session.sessionDate
        )

        sessionRepository.save(updatedSession)
    }

    /**
     * Handles SessionMarkedAsNoShow event to update session status to no-show.
     */
    @EventHandler
    @Transactional
    fun on(event: SessionMarkedAsNoShow) {
        logger.info("Handling SessionMarkedAsNoShow event for sessionId: ${event.sessionId}")

        val session = sessionRepository.findById(event.sessionId)
            .orElseThrow { IllegalArgumentException("Session not found: ${event.sessionId}") }
        
        val updatedSession = session.copy(
            status = "NO_SHOW",
            notes = event.reason
        )

        sessionRepository.save(updatedSession)
    }

    /**
     * Handles SessionCancelled event to update session status to cancelled.
     */
    @EventHandler
    @Transactional
    fun on(event: SessionCancelled) {
        logger.info("Handling SessionCancelled event for sessionId: ${event.sessionId}")

        val session = sessionRepository.findById(event.sessionId)
            .orElseThrow { IllegalArgumentException("Session not found: ${event.sessionId}") }

        val updatedSession = session.copy(
            status = "CANCELLED"
        )

        sessionRepository.save(updatedSession)
    }
}

