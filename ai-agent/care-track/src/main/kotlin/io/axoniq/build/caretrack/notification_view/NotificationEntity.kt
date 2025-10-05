package io.axoniq.build.caretrack.notification_view

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * JPA Entity representing a notification in the notification view.
 * This entity stores notification data for the Notification View component.
 */
@Entity
@Table(name = "notifications")
data class NotificationEntity(
    @Id
    @Column(name = "notification_id")
    val notificationId: String = "",

    @Column(name = "patient_id", nullable = false)
    val patientId: String = "",
    
    @Column(name = "acknowledged", nullable = false)
    val acknowledged: Boolean = false,

    @Column(name = "message", nullable = false)
    val message: String = "",
    
    @Column(name = "created_date", nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "notification_type", nullable = false)
    val notificationType: String = "",

    @Column(name = "priority")
    val priority: String? = null,

    @Column(name = "patient_name")
    val patientName: String? = null
)

