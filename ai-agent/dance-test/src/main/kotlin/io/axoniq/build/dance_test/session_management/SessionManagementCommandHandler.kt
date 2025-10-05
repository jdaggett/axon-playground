package io.axoniq.build.dance_test.session_management

import io.axoniq.build.dance_test.session_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * Command handler for the Session Management component.
 * Handles session modification, completion and no-show processing.
 */
class SessionManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SessionManagementCommandHandler::class.java)
    }

    /**
     * Handles the CompleteTrainingSession command.
     * Completes a training session with actual duration and optional notes.
     */
    @CommandHandler
    fun handle(
        command: CompleteTrainingSession,
        @InjectEntity state: SessionManagementState,
        eventAppender: EventAppender
    ): SessionCompletionResult {
        logger.info("Handling CompleteTrainingSession command for session: ${command.sessionId}")
        
        // Validate that the session exists and can be completed
        if (state.getStatus() == "COMPLETED") {
            throw IllegalStateException("Session ${command.sessionId} has already been completed")
        }
        
        if (state.getStatus() == "NO_SHOW") {
            throw IllegalStateException("Cannot complete a session marked as no-show")
        }

        // Append session completion event
        eventAppender.append(
            SessionCompleted(
                sessionId = command.sessionId,
                actualDuration = command.actualDuration,
                completionDate = LocalDateTime.now()
            )
        )
        
        // Append lesson balance decrease event
        eventAppender.append(
            LessonBalanceDecreasedFromSession(
                studentId = state.getStudentId()!!,
                sessionId = command.sessionId,
                lessonsUsed = 1
            )
        )

        // Record notes if provided
        if (!command.notes.isNullOrBlank()) {
            eventAppender.append(
                SessionNotesRecorded(
                    sessionId = command.sessionId,
                    notes = command.notes
                )
            )
        }

        logger.info("Successfully completed training session: ${command.sessionId}")
        return SessionCompletionResult(success = true)
    }

    /**
     * Handles the ModifySessionDetails command.
     * Modifies session details such as duration and session date.
     */
    @CommandHandler
    fun handle(
        command: ModifySessionDetails,
        @InjectEntity state: SessionManagementState,
        eventAppender: EventAppender
    ): SessionModificationResult {
        logger.info("Handling ModifySessionDetails command for session: ${command.sessionId}")

        // Validate that the session can be modified
        if (state.getStatus() == "COMPLETED") {
            throw IllegalStateException("Cannot modify a completed session")
        }

        if (state.getStatus() == "NO_SHOW") {
            throw IllegalStateException("Cannot modify a session marked as no-show")
        }

        // Append session details modification event
        eventAppender.append(
            SessionDetailsModified(
                sessionId = command.sessionId,
                newDuration = command.newDuration,
                newSessionDate = command.newSessionDate
            )
        )

        logger.info("Successfully modified session details for: ${command.sessionId}")
        return SessionModificationResult(success = true)
    }

    /**
     * Handles the MarkSessionAsNoShow command.
     * Marks a session as no-show with optional charging.
     */
    @CommandHandler
    fun handle(
        command: MarkSessionAsNoShow,
        @InjectEntity state: SessionManagementState,
        eventAppender: EventAppender
    ): NoShowResult {
        logger.info("Handling MarkSessionAsNoShow command for session: ${command.sessionId}")

        // Validate that the session can be marked as no-show
        if (state.getStatus() == "COMPLETED") {
            throw IllegalStateException("Cannot mark a completed session as no-show")
        }

        if (state.getStatus() == "NO_SHOW") {
            throw IllegalStateException("Session ${command.sessionId} is already marked as no-show")
        }

        // Append session marked as no-show event
        eventAppender.append(
            SessionMarkedAsNoShow(
                sessionId = command.sessionId,
                reason = command.reason,
                chargeStudent = command.chargeStudent
            )
        )
        
        logger.info("Successfully marked session as no-show: ${command.sessionId}")
        return NoShowResult(success = true)
    }

    /**
     * Handles the CompleteSessionWithReducedTime command.
     * Completes a session with reduced time but applies full charge.
     */
    @CommandHandler
    fun handle(
        command: CompleteSessionWithReducedTime,
        @InjectEntity state: SessionManagementState,
        eventAppender: EventAppender
    ): ReducedTimeSessionResult {
        logger.info("Handling CompleteSessionWithReducedTime command for session: ${command.sessionId}")

        // Validate that the session exists and can be completed
        if (state.getStatus() == "COMPLETED") {
            throw IllegalStateException("Session ${command.sessionId} has already been completed")
        }

        if (state.getStatus() == "NO_SHOW") {
            throw IllegalStateException("Cannot complete a session marked as no-show")
        }

        // Append session completion with full charge event
        eventAppender.append(
            SessionCompletedWithFullCharge(
                sessionId = command.sessionId,
                actualDuration = command.actualDuration,
                fullChargeApplied = true
            )
        )

        // Record notes if provided
        if (!command.notes.isNullOrBlank()) {
            eventAppender.append(
                SessionNotesRecorded(
                    sessionId = command.sessionId,
                    notes = command.notes
                )
            )
        }

        logger.info("Successfully completed session with reduced time: ${command.sessionId}")
        return ReducedTimeSessionResult(success = true)
    }
}

