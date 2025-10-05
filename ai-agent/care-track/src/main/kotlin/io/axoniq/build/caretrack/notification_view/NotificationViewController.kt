package io.axoniq.build.caretrack.notification_view

import io.axoniq.build.caretrack.notification_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for the Notification View component.
 * Exposes HTTP endpoints to query notification data through the QueryGateway.
 */
@RestController
@RequestMapping("/api/notifications")
class NotificationViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(NotificationViewController::class.java)
    }

    /**
     * GET endpoint to retrieve detailed information about a specific urgent notification.
     * Part of the Notification View component's REST API.
     */
    @GetMapping("/urgent/{notificationId}")
    fun getUrgentNotificationDetails(@PathVariable notificationId: String): CompletableFuture<UrgentNotificationDetailsResult> {
        logger.info("REST request for urgent notification details: $notificationId")
        val query = UrgentNotificationDetails(notificationId)
        return queryGateway.query(query, UrgentNotificationDetailsResult::class.java, null)
    }

    /**
     * GET endpoint to retrieve urgent health notifications for a specific patient and family member.
     * Part of the Notification View component's REST API.
     */
    @GetMapping("/urgent")
    fun getUrgentHealthNotifications(
        @RequestParam patientId: String,
        @RequestParam familyMemberEmail: String
    ): CompletableFuture<UrgentHealthNotificationsResult> {
        logger.info("REST request for urgent health notifications - Patient: $patientId, Family Member: $familyMemberEmail")
        val query = UrgentHealthNotifications(patientId, familyMemberEmail)
        return queryGateway.query(query, UrgentHealthNotificationsResult::class.java, null)
    }
}