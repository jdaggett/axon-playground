package io.axoniq.build.apex_racing_labs.write.driver_rating

import io.axoniq.build.apex_racing_labs.driver_rating.DriverPerformanceRatingServiceCommandHandler
import io.axoniq.build.apex_racing_labs.driver_rating.DriverPerformanceRatingState
import io.axoniq.build.apex_racing_labs.driver_rating.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Axon test fixture tests for the Driver Performance Rating Service component.
 * Tests command handling scenarios using event sourcing.
 */
class DriverPerformanceRatingServiceAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, DriverPerformanceRatingState::class.java)
        
        val commandHandlingModule = CommandHandlingModule
            .named("DriverPerformanceRatingService")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> DriverPerformanceRatingServiceCommandHandler() }

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
    fun `given no prior activity, when rate driver performance with valid rating, then success`() {
        val command = RateDriverPerformance(
            userId = "user123",
            driverId = "driver456",
            raceId = "race789",
            rating = 8
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .events(
                DriverPerformanceRated(
                    userId = "user123",
                    driverId = "driver456",
                    raceId = "race789",
                    rating = 8
                )
            )
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(DriverRatingResult::class.java)
                val payload = result.payload() as DriverRatingResult
                assertThat(payload.success).isTrue
                assertThat(payload.message).isEqualTo("Driver performance rated successfully")
            }
    }

    @Test
    fun `given no prior activity, when rate driver performance with invalid rating below range, then failure`() {
        val command = RateDriverPerformance(
            userId = "user123",
            driverId = "driver456",
            raceId = "race789",
            rating = 0
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(DriverRatingResult::class.java)
                val payload = result.payload() as DriverRatingResult
                assertThat(payload.success).isFalse
                assertThat(payload.message).isEqualTo("Rating must be between 1 and 10")
            }
    }

    @Test
    fun `given no prior activity, when rate driver performance with invalid rating above range, then failure`() {
        val command = RateDriverPerformance(
            userId = "user123",
            driverId = "driver456",
            raceId = "race789",
            rating = 11
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(DriverRatingResult::class.java)
                val payload = result.payload() as DriverRatingResult
                assertThat(payload.success).isFalse
                assertThat(payload.message).isEqualTo("Rating must be between 1 and 10")
            }
    }

    @Test
    fun `given driver already rated by user, when rate driver performance again, then failure`() {
        val command = RateDriverPerformance(
            userId = "user123",
            driverId = "driver456",
            raceId = "race789",
            rating = 7
        )

        fixture.given()
            .event(
                DriverPerformanceRated(
                    userId = "user123",
                    driverId = "driver456",
                    raceId = "race789",
                    rating = 5
                )
            )
            .`when`()
            .command(command)
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(DriverRatingResult::class.java)
                val payload = result.payload() as DriverRatingResult
                assertThat(payload.success).isFalse
                assertThat(payload.message).isEqualTo("You have already rated this driver for this race")
            }
    }

    @Test
    fun `given driver rated by user, when update driver rating with valid rating, then success`() {
        val command = UpdateDriverRating(
            userId = "user123",
            driverId = "driver456",
            newRating = 9,
            raceId = "race789"
        )

        fixture.given()
            .event(
                DriverPerformanceRated(
                    userId = "user123",
                    driverId = "driver456",
                    raceId = "race789",
                    rating = 6
                )
            )
            .`when`()
            .command(command)
            .then()
            .success()
            .events(
                DriverRatingUpdated(
                    userId = "user123",
                    driverId = "driver456",
                    newRating = 9,
                    raceId = "race789",
                    previousRating = 6
                )
            )
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(DriverRatingUpdateResult::class.java)
                val payload = result.payload() as DriverRatingUpdateResult
                assertThat(payload.success).isTrue
                assertThat(payload.message).isEqualTo("Driver rating updated successfully")
            }
    }

    @Test
    fun `given no prior rating, when update driver rating, then failure`() {
        val command = UpdateDriverRating(
            userId = "user123",
            driverId = "driver456",
            newRating = 8,
            raceId = "race789"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(DriverRatingUpdateResult::class.java)
                val payload = result.payload() as DriverRatingUpdateResult
                assertThat(payload.success).isFalse
                assertThat(payload.message).isEqualTo("You have not rated this driver for this race yet")
            }
    }

    @Test
    fun `given driver rated by user, when update driver rating with invalid rating below range, then failure`() {
        val command = UpdateDriverRating(
            userId = "user123",
            driverId = "driver456",
            newRating = 0,
            raceId = "race789"
        )

        fixture.given()
            .event(
                DriverPerformanceRated(
                    userId = "user123",
                    driverId = "driver456",
                    raceId = "race789",
                    rating = 5
                )
            )
            .`when`()
            .command(command)
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(DriverRatingUpdateResult::class.java)
                val payload = result.payload() as DriverRatingUpdateResult
                assertThat(payload.success).isFalse
                assertThat(payload.message).isEqualTo("Rating must be between 1 and 10")
            }
    }

    @Test
    fun `given driver rated by user, when update driver rating with invalid rating above range, then failure`() {
        val command = UpdateDriverRating(
            userId = "user123",
            driverId = "driver456",
            newRating = 15,
            raceId = "race789"
        )

        fixture.given()
            .event(
                DriverPerformanceRated(
                    userId = "user123",
                    driverId = "driver456",
                    raceId = "race789",
                    rating = 7
                )
            )
            .`when`()
            .command(command)
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(DriverRatingUpdateResult::class.java)
                val payload = result.payload() as DriverRatingUpdateResult
                assertThat(payload.success).isFalse
                assertThat(payload.message).isEqualTo("Rating must be between 1 and 10")
            }
    }

    @Test
    fun `given multiple ratings by different users, when update specific user rating, then only that rating is updated`() {
        val command = UpdateDriverRating(
            userId = "user123",
            driverId = "driver456",
            newRating = 10,
            raceId = "race789"
        )

        fixture.given()
            .event(
                DriverPerformanceRated(
                    userId = "user123",
                    driverId = "driver456",
                    raceId = "race789",
                    rating = 4
                )
            )
            .event(
                DriverPerformanceRated(
                    userId = "user999",
                    driverId = "driver456",
                    raceId = "race789",
                    rating = 8
                )
            )
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as DriverRatingUpdated
                assertThat(event.userId).isEqualTo("user123")
                assertThat(event.driverId).isEqualTo("driver456")
                assertThat(event.raceId).isEqualTo("race789")
                assertThat(event.newRating).isEqualTo(10)
                assertThat(event.previousRating).isEqualTo(4)
            }
    }
}