package io.axoniq.build.dance_test.lesson_package_management

import io.axoniq.build.dance_test.lesson_package_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Lesson Package Management component.
 * Exposes endpoints for creating and deleting lesson packages.
 */
@RestController
@RequestMapping("/api/lesson-packages")
class LessonPackageManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LessonPackageManagementController::class.java)
    }

    /**
     * Creates a custom lesson package for a student with a specific instructor.
     */
    @PostMapping("/create")
    fun createCustomLessonPackage(@RequestBody request: CreateCustomLessonPackageRequest): ResponseEntity<String> {
        val command = CreateCustomLessonPackage(
            packageId = request.packageId,
            studentId = request.studentId,
            instructorId = request.instructorId,
            lessonCount = request.lessonCount,
            lessonDuration = request.lessonDuration,
            price = request.price
        )
        logger.info("Dispatching CreateCustomLessonPackage command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Custom lesson package creation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateCustomLessonPackage command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create custom lesson package")
        }
    }
    
    /**
     * Deletes a lesson package for a specific student.
     */
    @DeleteMapping("/{packageId}")
    fun deleteLessonPackage(
        @PathVariable packageId: String,
        @RequestParam studentId: String
    ): ResponseEntity<String> {
        val command = DeleteLessonPackage(
            packageId = packageId,
            studentId = studentId
        )
        logger.info("Dispatching DeleteLessonPackage command: $command")
        
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Lesson package deletion accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch DeleteLessonPackage command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete lesson package")
        }
    }
}

/**
 * Request DTO for creating a custom lesson package.
 */
data class CreateCustomLessonPackageRequest(
    val packageId: String,
    val studentId: String,
    val instructorId: String,
    val lessonCount: Int,
    val lessonDuration: Int,
    val price: Double
)

