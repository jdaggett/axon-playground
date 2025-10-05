package io.axoniq.build.dance_test.student_management

import io.axoniq.build.dance_test.student_management.api.*
import io.axoniq.build.dance_test.student_management.exception.CannotDeleteStudentWithOutstandingBalance
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Command handler for Student Management component.
 * Handles student profile creation, deletion and trainer-student relationships.
 */
class StudentManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StudentManagementCommandHandler::class.java)
    }

    /**
     * Handles DeleteStudentProfile command.
     * Validates that student exists and has no outstanding balance before deletion.
     */
    @CommandHandler
    fun handle(
        command: DeleteStudentProfile,
        @InjectEntity state: StudentManagementState,
        eventAppender: EventAppender
    ): StudentDeletionResult {
        logger.info("Handling DeleteStudentProfile command for student: ${command.studentId}")

        // Check if student has outstanding balance
        if (state.getHasOutstandingBalance()) {
            logger.error("Cannot delete student with outstanding balance: ${command.studentId}")
            throw CannotDeleteStudentWithOutstandingBalance("Cannot delete student with outstanding balance")
        }

        // Emit events for successful deletion
        eventAppender.append(StudentProfileDeleted(studentId = command.studentId))

        // If there's an active relationship, terminate it
        if (state.getRelationshipStatus() == "ACTIVE") {
            eventAppender.append(
                TrainerStudentRelationshipTerminated(
                    instructorId = command.instructorId,
                    studentId = command.studentId
                )
            )
        }

        logger.info("Successfully deleted student profile: ${command.studentId}")
        return StudentDeletionResult(success = true)
    }

    /**
     * Handles CreateStudentProfile command.
     * Creates a new student profile and establishes trainer-student relationship.
     */
    @CommandHandler
    fun handle(
        command: CreateStudentProfile,
        @InjectEntity state: StudentManagementState,
        eventAppender: EventAppender
    ): StudentProfileResult {
        logger.info("Handling CreateStudentProfile command for student: ${command.studentId}")

        // Create student profile
        eventAppender.append(
            StudentProfileCreated(
                instructorId = command.instructorId,
                name = command.name,
                studentId = command.studentId,
                phone = command.phone
            )
        )

        // Establish trainer-student relationship
        eventAppender.append(
            TrainerStudentRelationshipEstablished(
                instructorId = command.instructorId,
                studentId = command.studentId
            )
        )

        logger.info("Successfully created student profile: ${command.studentId}")
        return StudentProfileResult(success = true, studentId = command.studentId)
    }
}

