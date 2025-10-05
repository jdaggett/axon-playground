package io.axoniq.build.jupiter_wheels.user_account_management

import io.axoniq.build.jupiter_wheels.user_account_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import java.time.LocalDateTime

/**
 * Event-sourced entity for User Account Management component.
 * Maintains state for user registration and email verification.
 */
@EventSourcedEntity
class UserAccountManagementState {
    private var userId: String? = null
    private var email: String? = null
    private var verificationToken: String? = null
    private var emailVerified: Boolean = false

    fun getUserId(): String? = userId
    fun getEmail(): String? = email
    fun getVerificationToken(): String? = verificationToken
    fun getEmailVerified(): Boolean = emailVerified

    @EntityCreator
    constructor()

    /**
     * Handles UserAccountCreated event to initialize user state.
     * This evolves the state when a new user account is created.
     */
    @EventSourcingHandler
    fun evolve(event: UserAccountCreated) {
        this.userId = event.userId
        this.email = event.email
        // Generate a verification token when account is created
        this.verificationToken = generateVerificationToken()
        this.emailVerified = false
    }

    /**
     * Handles EmailVerified event to mark email as verified.
     * This evolves the state when email verification is completed.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: EmailVerified) {
        this.emailVerified = true
        this.verificationToken = null // Clear token after verification
    }

    private fun generateVerificationToken(): String {
        return java.util.UUID.randomUUID().toString()
    }

    companion object {
        /**
         * Builds event criteria for loading user account events.
         * Queries events tagged with the userId to reconstruct user account state.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(userId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("User", userId))
                .andBeingOneOfTypes(
                    UserAccountCreated::class.java.name,
                    EmailVerified::class.java.name
                )
        }
    }
}

