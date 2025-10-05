package io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication

import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api.ParticipantAuthenticated
import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api.PasswordResetEmailSent
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced entity for Participant Authentication component.
 * Maintains participant authentication state including authentication status and credentials.
 */
@EventSourcedEntity
class ParticipantAuthenticationState {
    private var participantId: String? = null
    private var email: String? = null
    private var githubId: String? = null
    private var authenticationMethod: String? = null
    private var isAuthenticated: Boolean = false

    fun getParticipantId(): String? = participantId
    fun getEmail(): String? = email
    fun getGithubId(): String? = githubId
    fun getAuthenticationMethod(): String? = authenticationMethod
    fun getIsAuthenticated(): Boolean = isAuthenticated

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for ParticipantAuthenticated event.
     * Updates participant state when authentication occurs.
     */
    @EventSourcingHandler
    fun evolve(event: ParticipantAuthenticated) {
        this.participantId = event.participantId
        this.email = event.email
        this.authenticationMethod = event.authenticationMethod
        this.isAuthenticated = true

        if (event.authenticationMethod == "github") {
            // For GitHub authentication, we extract GitHub ID from participant ID
            this.githubId = event.participantId
        }
    }

    /**
     * Event sourcing handler for PasswordResetEmailSent event.
     * Updates participant state when password reset is requested.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: PasswordResetEmailSent) {
        // Password reset doesn't change authentication state
        // Event processed but no state changes needed
    }

    companion object {
        /**
         * Event criteria builder for loading participant authentication events.
         * Loads events tagged with the participant ID.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Participant", id))
                .andBeingOneOfTypes(
                    ParticipantAuthenticated::class.java.name,
                    PasswordResetEmailSent::class.java.name
                )
        }
    }
}

