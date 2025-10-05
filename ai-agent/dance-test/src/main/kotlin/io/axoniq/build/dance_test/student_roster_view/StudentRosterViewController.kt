package io.axoniq.build.dance_test.student_roster_view

import io.axoniq.build.dance_test.student_roster_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for Student Roster View component.
 * Exposes endpoints to query student roster and detailed student information.
 */
@RestController
@RequestMapping("/api/student-roster")
class StudentRosterViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StudentRosterViewController::class.java)
    }

    /**
     * GET endpoint to retrieve basic student details by student ID.
     */
    @GetMapping("/student/{studentId}")
    fun getStudentDetails(@PathVariable studentId: String): CompletableFuture<StudentDetailsData> {
        logger.info("REST request for student details, studentId: $studentId")
        val query = StudentDetails(studentId)
        return queryGateway.query(query, StudentDetailsData::class.java, null)
    }

    /**
     * GET endpoint to retrieve student roster for a specific instructor.
     */
    @GetMapping("/instructor/{instructorId}")
    fun getStudentRoster(@PathVariable instructorId: String): CompletableFuture<StudentRosterData> {
        logger.info("REST request for student roster, instructorId: $instructorId")
        val query = StudentRoster(instructorId)
        return queryGateway.query(query, StudentRosterData::class.java, null)
    }

    /**
     * GET endpoint to retrieve detailed student information for a specific instructor and student.
     */
    @GetMapping("/instructor/{instructorId}/student/{studentId}")
    fun getDetailedStudentInformation(
        @PathVariable instructorId: String,
        @PathVariable studentId: String
    ): CompletableFuture<DetailedStudentData> {
        logger.info("REST request for detailed student information, instructorId: $instructorId, studentId: $studentId")
        val query = DetailedStudentInformation(instructorId, studentId)
        return queryGateway.query(query, DetailedStudentData::class.java, null)
    }
}