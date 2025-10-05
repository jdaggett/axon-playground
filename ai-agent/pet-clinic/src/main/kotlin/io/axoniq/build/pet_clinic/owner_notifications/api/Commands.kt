package io.axoniq.build.pet_clinic.owner_notifications.api

/**
 * Owner Notifications - Command to notify an owner about their pet
 */
data class NotifyOwner(
    val ownerEmail: String,
    val petId: String
)

