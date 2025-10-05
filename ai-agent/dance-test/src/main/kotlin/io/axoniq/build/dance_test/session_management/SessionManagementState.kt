package io.axoniq.build.dance_test.session_management

import io.axoniq.build.dance_test.session_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import java.time.LocalDateTime

/**
 * Event-sourced entity representing the state of a training session in the Session Management component.
 * Maintains session information including duration, date, student, instructor, status, and notes.
 */
@EventSourcedEntity
class SessionManagementState {
    
    private var sessionId: String? = null
    private var duration: Int = 0
    private var sessionDate: LocalDateTime? = null
    private var notes: String? = null
    private var studentId: String? = null
    private var status: String = "SCHEDULED"
    private var instructorId: String? = null
    private var actualDuration: Int? = null

    fun getSessionId(): String? = sessionId
    fun getDuration(): Int = duration
    fun getSessionDate(): LocalDateTime? = sessionDate
    fun getNotes(): String? = notes
    fun getStudentId(): String? = studentId
    fun getStatus(): String = status
    fun getInstructorId(): String? = instructorId
    fun getActualDuration(): Int? = actualDuration

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for SessionScheduled event.
     * Initializes the session state when a session is scheduled.
     */
    @EventSourcingHandler
    fun evolve(event: SessionScheduled) {
        this.sessionId = event.sessionId
        this.duration = event.duration
        this.sessionDate = event.sessionDate
        this.studentId = event.studentId
        this.instructorId = event.instructorId
        this.status = "SCHEDULED"
    }

    /**
     * Event sourcing handler for SessionDetailsModified event.
     * Updates session details when they are modified.
     */
    @EventSourcingHandler
    fun evolve(event: SessionDetailsModified) {
        if (event.newDuration != null) {
            this.duration = event.newDuration
        }
        if (event.newSessionDate != null) {
            this.sessionDate = event.newSessionDate
        }
    }

    /**
     * Event sourcing handler for SessionMarkedAsNoShow event.
     * Updates the session status to no-show.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: SessionMarkedAsNoShow) {
        this.status = "NO_SHOW"
    }

    /**
     * Event sourcing handler for SessionCompleted event.
     * Updates the session status and actual duration when completed.
     */
    @EventSourcingHandler
    fun evolve(event: SessionCompleted) {
        this.status = "COMPLETED"
        this.actualDuration = event.actualDuration
    }

    /**
     * Event sourcing handler for SessionNotesRecorded event.
     * Records notes for the session.
     */
    @EventSourcingHandler
    fun evolve(event: SessionNotesRecorded) {
        this.notes = event.notes
    }

    /**
     * Event sourcing handler for SessionCompletedWithFullCharge event.
     * Updates the session status and actual duration when completed with full charge.
     */
    @EventSourcingHandler
    fun evolve(event: SessionCompletedWithFullCharge) {
        this.status = "COMPLETED"
        this.actualDuration = event.actualDuration
    }

    companion object {
        /**
         * Event criteria builder for loading session-related events.
         * Loads all events tagged with the specific session identifier.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(sessionId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Session", sessionId))
                .andBeingOneOfTypes(
                    SessionScheduled::class.java.name,
                    SessionDetailsModified::class.java.name,
                    SessionMarkedAsNoShow::class.java.name,
                    SessionCompleted::class.java.name,
                    SessionNotesRecorded::class.java.name,
                    SessionCompletedWithFullCharge::class.java.name
                )
        }
    }
}

