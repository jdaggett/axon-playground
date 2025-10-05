package io.axoniq.build.pet_clinic.owner_notifications.api

/**
 * Owner Notifications - Event indicating an owner has been notified
 */
data class OwnerNotified(
    val ownerEmail: String,
    val petId: String,
    val notificationStatus: String
)

