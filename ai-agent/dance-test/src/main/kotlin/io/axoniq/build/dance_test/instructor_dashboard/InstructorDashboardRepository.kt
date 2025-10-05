package io.axoniq.build.dance_test.instructor_dashboard

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

/**
 * Repository interface for InstructorDashboardEntity operations.
 * Provides data access methods for the Instructor Dashboard component.
 */
interface InstructorDashboardRepository : JpaRepository<InstructorDashboardEntity, String>

/**
 * Repository interface for DashboardSessionEntity operations.
 * Handles session-related queries for dashboard analytics.
 */
interface DashboardSessionRepository : JpaRepository<DashboardSessionEntity, String> {

    /**
     * Finds upcoming sessions for an instructor that are not cancelled.
     */
    @Query("SELECT s FROM DashboardSessionEntity s WHERE s.instructorId = :instructorId AND s.sessionDate > :currentTime AND s.cancelled = false ORDER BY s.sessionDate ASC")
    fun findUpcomingSessionsByInstructor(@Param("instructorId") instructorId: String, @Param("currentTime") currentTime: LocalDateTime): List<DashboardSessionEntity>

    /**
     * Finds recent sessions for activity tracking.
     */
    @Query("SELECT s FROM DashboardSessionEntity s WHERE s.instructorId = :instructorId AND s.sessionDate >= :sinceDate ORDER BY s.sessionDate DESC")
    fun findRecentSessionsByInstructor(@Param("instructorId") instructorId: String, @Param("sinceDate") sinceDate: LocalDateTime): List<DashboardSessionEntity>
}

/**
 * Repository interface for DashboardStudentPaymentEntity operations.
 * Handles payment-related queries for balance calculations.
 */
interface DashboardStudentPaymentRepository : JpaRepository<DashboardStudentPaymentEntity, Long> {

    /**
     * Finds the latest payment for each student.
     */
    @Query("SELECT p FROM DashboardStudentPaymentEntity p WHERE p.paymentDate = (SELECT MAX(p2.paymentDate) FROM DashboardStudentPaymentEntity p2 WHERE p2.studentId = p.studentId)")
    fun findLatestPaymentsByStudent(): List<DashboardStudentPaymentEntity>
}

/**
 * Repository interface for DashboardStudentActivityEntity operations.
 * Handles student activity queries for dashboard display.
 */
interface DashboardStudentActivityRepository : JpaRepository<DashboardStudentActivityEntity, Long> {

    /**
     * Finds recent activities for an instructor.
     */
    @Query("SELECT a FROM DashboardStudentActivityEntity a WHERE a.instructorId = :instructorId AND a.activityDate >= :sinceDate ORDER BY a.activityDate DESC")
    fun findRecentActivitiesByInstructor(@Param("instructorId") instructorId: String, @Param("sinceDate") sinceDate: LocalDateTime): List<DashboardStudentActivityEntity>
}

/**
 * Repository interface for DashboardBookingNotificationEntity operations.
 * Handles booking notification queries for dashboard alerts.
 */
interface DashboardBookingNotificationRepository : JpaRepository<DashboardBookingNotificationEntity, Long> {

    /**
     * Finds recent booking notifications for an instructor.
     */
    @Query("SELECT n FROM DashboardBookingNotificationEntity n WHERE n.instructorId = :instructorId ORDER BY n.notificationDate DESC")
    fun findByInstructorIdOrderByNotificationDateDesc(@Param("instructorId") instructorId: String): List<DashboardBookingNotificationEntity>
}

