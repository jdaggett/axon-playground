package io.axoniq.build.caretrack.notification_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for NotificationEntity.
 * Provides data access methods for the Notification View component.
 */
@Repository
interface NotificationRepository : JpaRepository<NotificationEntity, String> {

    /**
     * Finds all notifications for a specific patient.
     * Used by the Notification View component to retrieve patient-specific notifications.
     */
    fun findByPatientIdOrderByCreatedDateDesc(patientId: String): List<NotificationEntity>

    /**
     * Finds notifications by patient ID and priority level.
     * Used by the Notification View component to filter urgent notifications.
     */
    @Query("SELECT n FROM NotificationEntity n WHERE n.patientId = :patientId AND n.priority = :priority ORDER BY n.createdDate DESC")
    fun findByPatientIdAndPriority(@Param("patientId") patientId: String, @Param("priority") priority: String): List<NotificationEntity>
}

