package io.axoniq.build.dance_test.lesson_package_management

import io.axoniq.build.dance_test.lesson_package_management.api.*
import io.axoniq.build.dance_test.lesson_package_management.exception.CannotDeleteActiveLessonPackage
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Command handler for the Lesson Package Management component.
 * Handles creation and deletion of custom lesson packages.
 */
class LessonPackageManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LessonPackageManagementCommandHandler::class.java)
    }

    /**
     * Handles CreateCustomLessonPackage command to create a new lesson package.
     * Creates the package, increases student lesson balance, and records the transaction.
     */
    @CommandHandler
    fun handle(
        command: CreateCustomLessonPackage,
        @InjectEntity state: LessonPackageManagementState,
        eventAppender: EventAppender
    ): LessonPackageResult {
        logger.info("Handling CreateCustomLessonPackage command for packageId: ${command.packageId}")

        // Create the lesson package
        val packageCreatedEvent = CustomLessonPackageCreated(
            packageId = command.packageId,
            lessonCount = command.lessonCount,
            lessonDuration = command.lessonDuration,
            studentId = command.studentId,
            instructorId = command.instructorId,
            price = command.price
        )
        eventAppender.append(packageCreatedEvent)
        
        // Increase lesson balance for the student
        val balanceIncreasedEvent = LessonBalanceIncreasedFromPackage(
            packageId = command.packageId,
            studentId = command.studentId,
            lessonCount = command.lessonCount
        )
        eventAppender.append(balanceIncreasedEvent)
        
        // Record the transaction
        val transactionEvent = TransactionRecordCreated(
            studentId = command.studentId,
            transactionType = "LESSON_PACKAGE_PURCHASE",
            amount = command.price,
            description = "Purchase of custom lesson package ${command.packageId}"
        )
        eventAppender.append(transactionEvent)

        logger.info("Successfully created custom lesson package: ${command.packageId}")
        return LessonPackageResult(success = true, packageId = command.packageId)
    }
    
    /**
     * Handles DeleteLessonPackage command to delete an existing lesson package.
     * Validates that the package is not active before allowing deletion.
     */
    @CommandHandler
    fun handle(
        command: DeleteLessonPackage,
        @InjectEntity state: LessonPackageManagementState,
        eventAppender: EventAppender
    ): LessonPackageDeletionResult {
        logger.info("Handling DeleteLessonPackage command for packageId: ${command.packageId}")

        // Validate that the package exists
        if (state.getPackageId() == null) {
            throw IllegalStateException("Lesson package with id ${command.packageId} does not exist")
        }
        
        // Validate that the package is not active
        if (state.getIsActive()) {
            throw CannotDeleteActiveLessonPackage("Cannot delete active lesson package ${command.packageId}")
        }
        
        // Delete the lesson package
        val packageDeletedEvent = LessonPackageDeleted(packageId = command.packageId)
        eventAppender.append(packageDeletedEvent)

        logger.info("Successfully deleted lesson package: ${command.packageId}")
        return LessonPackageDeletionResult(success = true)
    }
}

