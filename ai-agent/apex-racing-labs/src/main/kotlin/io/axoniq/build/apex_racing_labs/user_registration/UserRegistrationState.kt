package io.axoniq.build.apex_racing_labs.user_registration

import io.axoniq.build.apex_racing_labs.user_registration.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced entity for the User Registration Service component.
 * Maintains state for user account creation and email verification process.
 */
@EventSourcedEntity
class UserRegistrationState {

    private var emailVerified: Boolean = false
    private var email: String? = null
    private var accountStatus: String = "PENDING"
    private var verificationToken: String? = null

    /**
     * Gets the email verification status.
     * @return true if email is verified, false otherwise
     */
    fun getEmailVerified(): Boolean = emailVerified

    /**
     * Gets the user's email address.
     * @return the email address or null if not set
     */
    fun getEmail(): String? = email

    /**
     * Gets the current account status.
     * @return the account status (PENDING, VERIFIED, UNVERIFIED)
     */
    fun getAccountStatus(): String = accountStatus

    /**
     * Gets the verification token.
     * @return the verification token or null if not set
     */
    fun getVerificationToken(): String? = verificationToken

    /**
     * Default constructor for entity creation.
     */
    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for AccountCreated events.
     * Initializes user registration state when account is created.
     * 
     * @param event The AccountCreated event containing user details
     */
    @EventSourcingHandler
    fun evolve(event: AccountCreated) {
        this.email = event.email
        this.verificationToken = event.verificationToken
        this.emailVerified = false
        this.accountStatus = "PENDING"
    }

    /**
     * Event sourcing handler for EmailVerified events.
     * Updates state when user email is successfully verified.
     * 
     * @param event The EmailVerified event
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: EmailVerified) {
        this.emailVerified = true
        this.accountStatus = "VERIFIED"
    }

    /**
     * Event sourcing handler for AccountMarkedUnverified events.
     * Updates state when account is marked as unverified due to expired verification.
     * 
     * @param event The AccountMarkedUnverified event
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: AccountMarkedUnverified) {
        this.accountStatus = "UNVERIFIED"
    }

    companion object {
        /**
         * Event criteria builder for loading user registration events.
         * Defines which events should be loaded based on the user email tag.
         * 
         * @param email The user email identifier
         * @return EventCriteria for loading relevant events
         */
        @EventCriteriaBuilder
        fun resolveCriteria(email: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("User", email))
                .andBeingOneOfTypes(
                    AccountCreated::class.java.name,
                    EmailVerified::class.java.name,
                    AccountMarkedUnverified::class.java.name
                )
        }
    }
}

