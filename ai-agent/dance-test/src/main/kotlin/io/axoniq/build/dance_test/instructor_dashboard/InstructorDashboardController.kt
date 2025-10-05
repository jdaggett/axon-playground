package io.axoniq.build.dance_test.instructor_dashboard

import io.axoniq.build.dance_test.instructor_dashboard.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the Instructor Dashboard component.
 * Provides HTTP endpoints to access instructor dashboard data and analytics.
 */
@RestController
@RequestMapping("/api/instructor-dashboard")
class InstructorDashboardController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InstructorDashboardController::class.java)
    }

    /**
     * Retrieves outstanding balances dashboard for an instructor.
     * Shows students with negative balances requiring attention.
     */
    @GetMapping("/{instructorId}/outstanding-balances")
    fun getOutstandingBalances(@PathVariable instructorId: String): CompletableFuture<OutstandingBalancesData> {
        logger.info("REST request for outstanding balances dashboard for instructor: $instructorId")
        val query = OutstandingBalancesDashboard(instructorId)
        return queryGateway.query(query, OutstandingBalancesData::class.java, null)
    }

    /**
     * Retrieves recent student activity for an instructor.
     * Shows recent student activities and interactions.
     */
    @GetMapping("/{instructorId}/recent-activity")
    fun getRecentActivity(@PathVariable instructorId: String): CompletableFuture<RecentActivityData> {
        logger.info("REST request for recent student activity for instructor: $instructorId")
        val query = RecentStudentActivity(instructorId)
        return queryGateway.query(query, RecentActivityData::class.java, null)
    }

    /**
     * Retrieves main instructor dashboard data.
     * Shows comprehensive dashboard information including upcoming sessions,
     * recent activity, and student statistics.
     */
    @GetMapping("/{instructorId}")
    fun getInstructorDashboard(@PathVariable instructorId: String): CompletableFuture<InstructorDashboardData> {
        logger.info("REST request for instructor dashboard for instructor: $instructorId")
        val query = InstructorDashboard(instructorId)
        return queryGateway.query(query, InstructorDashboardData::class.java, null)
    }

    /**
     * Retrieves booking notifications for an instructor.
     * Shows recent booking notifications requiring instructor attention.
     */
    @GetMapping("/{instructorId}/notifications")
    fun getBookingNotifications(@PathVariable instructorId: String): CompletableFuture<BookingNotificationData> {
        logger.info("REST request for booking notifications for instructor: $instructorId")
        val query = BookingNotifications(instructorId)
        return queryGateway.query(query, BookingNotificationData::class.java, null)
    }
}