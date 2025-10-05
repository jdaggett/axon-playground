package io.axoniq.build.dance_test.student_roster_view

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repository interface for Student Roster View component.
 * Provides data access methods for student roster entities.
 */
interface StudentRosterRepository : JpaRepository<StudentRosterEntity, String> {

    /**
     * Finds all students for a specific instructor.
     * Used by StudentRoster query handler.
     */
    fun findByInstructorId(instructorId: String): List<StudentRosterEntity>

    /**
     * Finds a specific student for a specific instructor.
     * Used by DetailedStudentInformation query handler.
     */
    fun findByInstructorIdAndStudentId(instructorId: String, studentId: String): StudentRosterEntity?
}

