package io.axoniq.build.apex_racing_labs.race_rating.write.race_rating

import io.axoniq.build.apex_racing_labs.race_rating.RaceRatingServiceCommandHandler
import io.axoniq.build.apex_racing_labs.race_rating.RaceRatingState
import io.axoniq.build.apex_racing_labs.race_rating.api.*
import io.axoniq.build.apex_racing_labs.race_rating.exception.CannotRateCancelledRace
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

/**
 * Test class for Race Rating Service using Axon Test Fixture.
 * Verifies the command handling behavior for race rating operations.
 */
class RaceRatingServiceAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, RaceRatingState::class.java)
        
        val commandHandlingModule = CommandHandlingModule
            .named("RaceRatingService")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> RaceRatingServiceCommandHandler() }

        configurer = configurer.registerEntity(stateEntity)
            .registerCommandHandlingModule(commandHandlingModule)
            .componentRegistry { cr -> cr.disableEnhancer(AxonServerConfigurationEnhancer::class.java) }
        fixture = AxonTestFixture.with(configurer)
    }

    @AfterEach
    fun afterEach() {
        fixture.stop()
    }

    @Test
    fun `given no prior activity, when rate race, then race rated event is published`() {
        val raceId = "race-123"
        val userId = "user-456"
        val rating = 5
        val comment = "Great race!"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RateRace(raceId, userId, comment, rating))
            .then()
            .success()
            .events(RaceRated(raceId, userId, comment, rating))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RaceRatingResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Race rated successfully")
            }
    }

    @Test
    fun `given race already rated by user, when rate race again, then new rating event is published`() {
        val raceId = "race-123"
        val userId = "user-456"
        val initialRating = 3
        val newRating = 5
        val comment = "Changed my mind - excellent race!"

        fixture.given()
            .event(RaceRated(raceId, userId, "Average race", initialRating))
            .`when`()
            .command(RateRace(raceId, userId, comment, newRating))
            .then()
            .success()
            .events(RaceRated(raceId, userId, comment, newRating))
    }

    @Test
    fun `given race is cancelled, when rate race, then CannotRateCancelledRace exception is thrown`() {
        val raceId = "race-123"
        val userId = "user-456"
        val rating = 4
        
        fixture.given()
            .event(RaceCancelled(raceId))
            .`when`()
            .command(RateRace(raceId, userId, "Good race", rating))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(CannotRateCancelledRace::class.java)
                    .hasMessageContaining("Cannot rate a cancelled race")
            }
    }

    @Test
    fun `given race rated and then cancelled, when rate race, then CannotRateCancelledRace exception is thrown`() {
        val raceId = "race-123"
        val userId = "user-456"
        val initialRating = 4
        val newRating = 5

        fixture.given()
            .event(RaceRated(raceId, userId, "Good race", initialRating))
            .event(RaceCancelled(raceId))
            .`when`()
            .command(RateRace(raceId, "user-789", "Trying to rate", newRating))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(CannotRateCancelledRace::class.java)
                    .hasMessageContaining("Cannot rate a cancelled race")
            }
    }

    @Test
    fun `given multiple users rated race, when new user rates race, then race rated event is published`() {
        val raceId = "race-123"
        val user1 = "user-111"
        val user2 = "user-222"
        val user3 = "user-333"

        fixture.given()
            .event(RaceRated(raceId, user1, "Amazing!", 5))
            .event(RaceRated(raceId, user2, "Not bad", 3))
            .`when`()
            .command(RateRace(raceId, user3, "Excellent racing!", 5))
            .then()
            .success()
            .events(RaceRated(raceId, user3, "Excellent racing!", 5))
    }

    @Test
    fun `given no prior activity, when rate race without comment, then race rated event is published`() {
        val raceId = "race-123"
        val userId = "user-456"
        val rating = 4

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RateRace(raceId, userId, null, rating))
            .then()
            .success()
            .events(RaceRated(raceId, userId, null, rating))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RaceRatingResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Race rated successfully")
            }
    }
}