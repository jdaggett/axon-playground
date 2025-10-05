package io.axoniq.build.dance_test.instructor_dashboard

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * JPA entity representing the instructor dashboard data for the Instructor Dashboard component.
 * This entity stores aggregated information about an instructor's dashboard including
 * recent bookings count, upcoming sessions count, students with negative balances count,
 * and total active students count.
 */
@Entity
@Table(name = "instructor_dashboard")
data class InstructorDashboardEntity(
    @Id
    val instructorId: String,
    
    val recentBookings: Int = 0,
    val upcomingSessions: Int = 0,
    val studentsWithNegativeBalances: Int = 0,
    val totalActiveStudents: Int = 0
)

/**
 * JPA entity representing session information for dashboard queries.
 * Stores session details needed for dashboard views and analytics.
 */
@Entity
@Table(name = "dashboard_sessions")
data class DashboardSessionEntity(
    @Id
    val sessionId: String,

    val instructorId: String,
    val studentId: String,
    val sessionDate: LocalDateTime,
    val duration: Int,
    val cancelled: Boolean = false,
    val cancellationTime: LocalDateTime? = null,

    @Column(name = "student_name")
    val studentName: String = ""
)

/**
 * JPA entity representing student payment information for dashboard analytics.
 * Tracks payment history and balance information for outstanding balance calculations.
 */
@Entity
@Table(name = "dashboard_student_payments")
data class DashboardStudentPaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val studentId: String,
    val amount: Double,
    val paymentMethod: String,
    val paymentDate: LocalDate,

    @Column(name = "student_name")
    val studentName: String = ""
)

/**
 * JPA entity representing student activity for recent activity tracking.
 * Stores various types of student activities for dashboard display.
 */
@Entity
@Table(name = "dashboard_student_activities")
data class DashboardStudentActivityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val instructorId: String,
    val studentId: String,
    val activityType: String,
    val description: String,
    val activityDate: LocalDateTime,
    val studentName: String = ""
)

/**
 * JPA entity representing booking notifications for instructor dashboard.
 * Tracks new bookings that need to be displayed as notifications.
 */
@Entity
@Table(name = "dashboard_booking_notifications")
data class DashboardBookingNotificationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val instructorId: String,
    val sessionId: String,
    val sessionDate: LocalDateTime,
    val notificationDate: LocalDateTime,
    val studentName: String = ""
)