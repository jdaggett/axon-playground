package io.axoniq.build.dance_test.student_management

import io.axoniq.build.dance_test.student_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Student Management component.
 * Exposes endpoints for creating and deleting student profiles.
 */
@RestController
@RequestMapping("/api/student-management")
class StudentManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StudentManagementController::class.java)
    }

    /**
     * Endpoint to create a new student profile.
     */
    @PostMapping("/students")
    fun createStudentProfile(@RequestBody request: CreateStudentProfileRequest): ResponseEntity<String> {
        val command = CreateStudentProfile(
            instructorId = request.instructorId,
            name = request.name,
            studentId = request.studentId,
            phone = request.phone
        )
        logger.info("Dispatching CreateStudentProfile command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Student profile creation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateStudentProfile command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create student profile")
        }
    }

    /**
     * Endpoint to delete a student profile.
     */
    @DeleteMapping("/students/{studentId}")
    fun deleteStudentProfile(
        @PathVariable studentId: String,
        @RequestBody request: DeleteStudentProfileRequest
    ): ResponseEntity<String> {
        val command = DeleteStudentProfile(
            instructorId = request.instructorId,
            studentId = studentId
        )
        logger.info("Dispatching DeleteStudentProfile command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Student profile deletion accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch DeleteStudentProfile command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete student profile")
        }
    }
}

/**
 * Request model for creating student profile.
 */
data class CreateStudentProfileRequest(
    val instructorId: String,
    val name: String,
    val studentId: String,
    val phone: String
)

/**
 * Request model for deleting student profile.
 */
data class DeleteStudentProfileRequest(
    val instructorId: String
)

