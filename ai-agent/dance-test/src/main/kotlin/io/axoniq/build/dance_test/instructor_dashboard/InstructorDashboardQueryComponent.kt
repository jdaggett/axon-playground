package io.axoniq.build.dance_test.instructor_dashboard

import io.axoniq.build.dance_test.instructor_dashboard.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Query component for the Instructor Dashboard.
 * Handles queries for instructor dashboard views and analytics, and processes
 * events to maintain the dashboard read model.
 */
@Component
class InstructorDashboardQueryComponent(
    private val instructorDashboardRepository: InstructorDashboardRepository,
    private val sessionRepository: DashboardSessionRepository,
    private val paymentRepository: DashboardStudentPaymentRepository,
    private val activityRepository: DashboardStudentActivityRepository,
    private val notificationRepository: DashboardBookingNotificationRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InstructorDashboardQueryComponent::class.java)
    }

    /**
     * Handles OutstandingBalancesDashboard query to retrieve students with negative balances.
     * Returns data about students who have outstanding payment obligations.
     */
    @QueryHandler
    fun handle(query: OutstandingBalancesDashboard): OutstandingBalancesData {
        logger.info("Processing OutstandingBalancesDashboard query for instructor: ${query.instructorId}")
        
        // For this example, we'll create mock data for students with negative balances
        // In a real implementation, this would calculate actual balances based on payments and sessions
        val payments = paymentRepository.findLatestPaymentsByStudent()
        
        val studentsWithNegativeBalances = payments.map { payment ->
            // Mock calculation - in reality this would involve complex balance calculations
            val mockBalance = -50.0 // Simulating negative balance
            StudentBalance(
                studentId = payment.studentId,
                lastPaymentDate = payment.paymentDate,
                balance = mockBalance,
                studentName = payment.studentName
            )
        }.filter { it.balance < 0 }

        return OutstandingBalancesData(studentsWithNegativeBalances)
    }

    /**
     * Handles RecentStudentActivity query to retrieve recent student activities.
     * Returns activity data for dashboard display.
     */
    @QueryHandler
    fun handle(query: RecentStudentActivity): RecentActivityData {
        logger.info("Processing RecentStudentActivity query for instructor: ${query.instructorId}")

        val recentDate = LocalDateTime.now().minusDays(7)
        val activities = activityRepository.findRecentActivitiesByInstructor(query.instructorId, recentDate)

        val activityItems = activities.map { activity ->
            ActivityItem(
                activityType = activity.activityType,
                description = activity.description,
                activityDate = activity.activityDate,
                studentName = activity.studentName
            )
        }

        return RecentActivityData(activityItems)
    }

    /**
     * Handles InstructorDashboard query to retrieve main dashboard data.
     * Returns comprehensive dashboard information including upcoming sessions,
     * recent activity, and active student count.
     */
    @QueryHandler
    fun handle(query: InstructorDashboard): InstructorDashboardData {
        logger.info("Processing InstructorDashboard query for instructor: ${query.instructorId}")

        val currentTime = LocalDateTime.now()
        val recentDate = currentTime.minusDays(7)

        // Get upcoming sessions
        val upcomingSessions = sessionRepository.findUpcomingSessionsByInstructor(query.instructorId, currentTime)
        val sessionSummaries = upcomingSessions.take(5).map { session ->
            SessionSummary(
                sessionDate = session.sessionDate,
                sessionId = session.sessionId,
                studentName = session.studentName
            )
        }

        // Get recent activities
        val recentActivities = activityRepository.findRecentActivitiesByInstructor(query.instructorId, recentDate)
        val activitySummaries = recentActivities.take(5).map { activity ->
            ActivitySummary(
                activityType = activity.activityType,
                activityDate = activity.activityDate,
                studentName = activity.studentName
            )
        }

        // Get dashboard entity for active student count
        val dashboardEntity = instructorDashboardRepository.findById(query.instructorId)
            .orElse(InstructorDashboardEntity(query.instructorId))
        
        return InstructorDashboardData(
            upcomingSessions = sessionSummaries,
            recentActivity = activitySummaries,
            totalActiveStudents = dashboardEntity.totalActiveStudents
        )
    }

    /**
     * Handles BookingNotifications query to retrieve new booking notifications.
     * Returns recent booking notifications for the instructor.
     */
    @QueryHandler
    fun handle(query: BookingNotifications): BookingNotificationData {
        logger.info("Processing BookingNotifications query for instructor: ${query.instructorId}")

        val notifications = notificationRepository.findByInstructorIdOrderByNotificationDateDesc(query.instructorId)

        val bookingNotifications = notifications.map { notification ->
            BookingNotification(
                sessionDate = notification.sessionDate,
                notificationDate = notification.notificationDate,
                sessionId = notification.sessionId,
                studentName = notification.studentName
            )
        }

        return BookingNotificationData(bookingNotifications)
    }

    /**
     * Handles SessionScheduled event to update dashboard data when a session is scheduled.
     * Creates session entity and updates dashboard counters.
     */
    @EventHandler
    fun on(event: SessionScheduled) {
        logger.info("Processing SessionScheduled event for session: ${event.sessionId}")

        // Create session entity
        val sessionEntity = DashboardSessionEntity(
            sessionId = event.sessionId,
            instructorId = event.instructorId,
            studentId = event.studentId,
            sessionDate = event.sessionDate,
            duration = event.duration,
            studentName = "Student-${event.studentId}" // Mock student name
        )
        sessionRepository.save(sessionEntity)

        // Update dashboard counters
        updateDashboardCounters(event.instructorId)
        
        // Create booking notification
        val notification = DashboardBookingNotificationEntity(
            instructorId = event.instructorId,
            sessionId = event.sessionId,
            sessionDate = event.sessionDate,
            notificationDate = LocalDateTime.now(),
            studentName = sessionEntity.studentName
        )
        notificationRepository.save(notification)

        // Create activity record
        val activity = DashboardStudentActivityEntity(
            instructorId = event.instructorId,
            studentId = event.studentId,
            activityType = "SESSION_SCHEDULED",
            description = "Session scheduled for ${event.sessionDate}",
            activityDate = LocalDateTime.now(),
            studentName = sessionEntity.studentName
        )
        activityRepository.save(activity)
    }

    /**
     * Handles SessionCancelled event to update dashboard data when a session is cancelled.
     * Updates session entity and dashboard counters.
     */
    @EventHandler
    fun on(event: SessionCancelled) {
        logger.info("Processing SessionCancelled event for session: ${event.sessionId}")
        
        // Update session entity
        sessionRepository.findById(event.sessionId).ifPresent { session ->
            val updatedSession = session.copy(
                cancelled = true,
                cancellationTime = event.cancellationTime
            )
            sessionRepository.save(updatedSession)
            
            // Update dashboard counters
            updateDashboardCounters(session.instructorId)
            
            // Create activity record
            val activity = DashboardStudentActivityEntity(
                instructorId = session.instructorId,
                studentId = session.studentId,
                activityType = "SESSION_CANCELLED",
                description = "Session cancelled at ${event.cancellationTime}",
                activityDate = event.cancellationTime,
                studentName = session.studentName
            )
            activityRepository.save(activity)
        }
    }

    /**
     * Handles PaymentRecorded event to update payment tracking for balance calculations.
     * Records payment information for outstanding balance analysis.
     */
    @EventHandler
    fun on(event: PaymentRecorded) {
        logger.info("Processing PaymentRecorded event for student: ${event.studentId}")
        
        val paymentEntity = DashboardStudentPaymentEntity(
            studentId = event.studentId,
            amount = event.amount,
            paymentMethod = event.paymentMethod,
            paymentDate = event.paymentDate,
            studentName = "Student-${event.studentId}" // Mock student name
        )
        paymentRepository.save(paymentEntity)

        // Create activity record for payment
        val activity = DashboardStudentActivityEntity(
            instructorId = "", // Would need instructor context from session data
            studentId = event.studentId,
            activityType = "PAYMENT_RECORDED",
            description = "Payment of ${event.amount} recorded via ${event.paymentMethod}",
            activityDate = event.paymentDate.atStartOfDay(),
            studentName = paymentEntity.studentName
        )
        activityRepository.save(activity)
    }

    /**
     * Handles BookingAccessBlocked event to track when student booking access is blocked.
     * Records blocking activity for dashboard display.
     */
    @EventHandler
    fun on(event: BookingAccessBlocked) {
        logger.info("Processing BookingAccessBlocked event for student: ${event.studentId}")
        
        val activity = DashboardStudentActivityEntity(
            instructorId = event.instructorId,
            studentId = event.studentId,
            activityType = "BOOKING_ACCESS_BLOCKED",
            description = "Booking access blocked: ${event.blockingReason}",
            activityDate = LocalDateTime.now(),
            studentName = "Student-${event.studentId}" // Mock student name
        )
        activityRepository.save(activity)

        // Update dashboard counters
        updateDashboardCounters(event.instructorId)
    }

    /**
     * Updates dashboard counters for an instructor based on current data.
     * Recalculates upcoming sessions, recent bookings, and student counts.
     */
    private fun updateDashboardCounters(instructorId: String) {
        logger.debug("Updating dashboard counters for instructor: $instructorId")
        
        val currentTime = LocalDateTime.now()
        val recentDate = currentTime.minusDays(7)

        val upcomingSessionsCount = sessionRepository.findUpcomingSessionsByInstructor(instructorId, currentTime).size
        val recentSessionsCount = sessionRepository.findRecentSessionsByInstructor(instructorId, recentDate).size
        val recentActivitiesCount = activityRepository.findRecentActivitiesByInstructor(instructorId, recentDate).size

        // Mock calculation for students with negative balances and active students
        val studentsWithNegativeBalances = 2 // Mock value
        val totalActiveStudents = 15 // Mock value

        val dashboardEntity = InstructorDashboardEntity(
            instructorId = instructorId,
            recentBookings = recentSessionsCount,
            upcomingSessions = upcomingSessionsCount,
            studentsWithNegativeBalances = studentsWithNegativeBalances,
            totalActiveStudents = totalActiveStudents
        )

        instructorDashboardRepository.save(dashboardEntity)
    }
}

