package io.axoniq.build.dance_test.instructor_management

import io.axoniq.build.dance_test.instructor_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * InstructorManagementCommandHandler - Handles commands for the Instructor Management component.
 *
 * This handler processes commands related to instructor profile creation and Calendly integration,
 * validating business rules and publishing appropriate events.
 */
class InstructorManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InstructorManagementCommandHandler::class.java)
    }

    /**
     * Handles CreateInstructorProfile command to create a new instructor profile.
     * 
     * @param command The CreateInstructorProfile command containing profile details
     * @param state The current instructor management state
     * @param eventAppender Event appender for publishing events
     * @return InstructorProfileResult indicating success and instructor ID
     */
    @CommandHandler
    fun handle(
        command: CreateInstructorProfile,
        @InjectEntity state: InstructorManagementState,
        eventAppender: EventAppender
    ): InstructorProfileResult {
        logger.info("Handling CreateInstructorProfile command for instructor: ${command.instructorId}")

        // Validate that instructor profile doesn't already exist
        if (state.getInstructorId() != null) {
            logger.warn("Instructor profile already exists for ID: ${command.instructorId}")
            return InstructorProfileResult(
                instructorId = command.instructorId,
                success = false
            )
        }

        // Create instructor profile created event
        val event = InstructorProfileCreated(
            instructorId = command.instructorId,
            email = command.email,
            phone = command.phone,
            specialties = command.specialties
        )

        eventAppender.append(event)
        logger.info("InstructorProfileCreated event appended for instructor: ${command.instructorId}")

        return InstructorProfileResult(
            instructorId = command.instructorId,
            success = true
        )
    }

    /**
     * Handles ConnectCalendlyIntegration command to connect Calendly integration.
     * 
     * @param command The ConnectCalendlyIntegration command
     * @param state The current instructor management state
     * @param eventAppender Event appender for publishing events
     * @return CalendlyIntegrationResult indicating success and integration ID
     */
    @CommandHandler
    fun handle(
        command: ConnectCalendlyIntegration,
        @InjectEntity state: InstructorManagementState,
        eventAppender: EventAppender
    ): CalendlyIntegrationResult {
        logger.info("Handling ConnectCalendlyIntegration command for instructor: ${command.instructorId}")

        // Validate that instructor profile exists
        if (state.getInstructorId() == null) {
            logger.warn("Cannot connect Calendly integration - instructor profile does not exist for ID: ${command.instructorId}")
            return CalendlyIntegrationResult(
                success = false,
                integrationId = ""
            )
        }

        // Create Calendly integration connected event
        val event = CalendlyIntegrationConnected(
            instructorId = command.instructorId,
            calendlyAccountId = command.calendlyAccountId
        )

        eventAppender.append(event)
        logger.info("CalendlyIntegrationConnected event appended for instructor: ${command.instructorId}")

        return CalendlyIntegrationResult(
            success = true,
            integrationId = command.calendlyAccountId
        )
    }

    /**
     * Handles UpdateCalendlySettings command to update Calendly availability settings.
     * 
     * @param command The UpdateCalendlySettings command
     * @param state The current instructor management state
     * @param eventAppender Event appender for publishing events
     * @return CalendlySettingsResult indicating success
     */
    @CommandHandler
    fun handle(
        command: UpdateCalendlySettings,
        @InjectEntity state: InstructorManagementState,
        eventAppender: EventAppender
    ): CalendlySettingsResult {
        logger.info("Handling UpdateCalendlySettings command for instructor: ${command.instructorId}")

        // Validate that Calendly integration exists
        if (state.getCalendlyIntegrationStatus() != "CONNECTED") {
            logger.warn("Cannot update Calendly settings - integration not connected for instructor: ${command.instructorId}")
            return CalendlySettingsResult(success = false)
        }

        // Create Calendly settings updated event
        val event = CalendlySettingsUpdated(
            instructorId = command.instructorId,
            calendlyAccountId = command.calendlyAccountId
        )

        eventAppender.append(event)
        logger.info("CalendlySettingsUpdated event appended for instructor: ${command.instructorId}")

        return CalendlySettingsResult(success = true)
    }
}

