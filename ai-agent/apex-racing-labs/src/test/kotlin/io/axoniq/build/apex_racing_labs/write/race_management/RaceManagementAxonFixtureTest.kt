package io.axoniq.build.apex_racing_labs.write.race_management

import io.axoniq.build.apex_racing_labs.race_management.RaceManagementCommandHandler
import io.axoniq.build.apex_racing_labs.race_management.RaceManagementState
import io.axoniq.build.apex_racing_labs.race_management.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Tests for Race Management Service using Axon Test Fixture
 * Verifies command handling, event publishing, and state management.
 */
class RaceManagementAxonFixtureTest {
    
    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, RaceManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("RaceManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> RaceManagementCommandHandler() }

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
    fun `given no prior activity, when create race, then race created event published`() {
        val raceId = "race-001"
        val participatingDriverIds = listOf("driver-1", "driver-2", "driver-3")
        val raceDate = LocalDate.of(2024, 6, 15)
        val trackName = "Monaco Grand Prix Circuit"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CreateRace(
                raceId = raceId,
                participatingDriverIds = participatingDriverIds,
                raceDate = raceDate,
                trackName = trackName
            ))
            .then()
            .success()
            .events(RaceCreated(
                raceId = raceId,
                participatingDriverIds = participatingDriverIds,
                raceDate = raceDate,
                trackName = trackName
            ))
    }

    @Test
    fun `given race already exists, when create race with same id, then creation fails`() {
        val raceId = "race-001"
        val participatingDriverIds = listOf("driver-1", "driver-2")
        val raceDate = LocalDate.of(2024, 6, 15)
        val trackName = "Monaco Grand Prix Circuit"

        fixture.given()
            .event(RaceCreated(
                raceId = raceId,
                participatingDriverIds = participatingDriverIds,
                raceDate = raceDate,
                trackName = trackName
            ))
            .`when`()
            .command(CreateRace(
                raceId = raceId,
                participatingDriverIds = listOf("driver-3", "driver-4"),
                raceDate = LocalDate.of(2024, 7, 20),
                trackName = "Silverstone Circuit"
            ))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RaceCreationResult
                assertThat(payload.success).isFalse()
                assertThat(payload.message).contains("already exists")
            }
    }

    @Test
    fun `given race created, when cancel race, then race cancelled event published`() {
        val raceId = "race-001"
        val participatingDriverIds = listOf("driver-1", "driver-2", "driver-3")
        val raceDate = LocalDate.of(2024, 6, 15)
        val trackName = "Monaco Grand Prix Circuit"

        fixture.given()
            .event(RaceCreated(
                raceId = raceId,
                participatingDriverIds = participatingDriverIds,
                raceDate = raceDate,
                trackName = trackName
            ))
            .`when`()
            .command(CancelRace(raceId = raceId))
            .then()
            .success()
            .events(RaceCancelled(raceId = raceId))
    }

    @Test
    fun `given no race exists, when cancel race, then cancellation fails`() {
        val raceId = "race-001"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CancelRace(raceId = raceId))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RaceCancellationResult
                assertThat(payload.success).isFalse()
                assertThat(payload.message).contains("does not exist")
            }
    }

    @Test
    fun `given race already cancelled, when cancel race again, then cancellation fails`() {
        val raceId = "race-001"
        val participatingDriverIds = listOf("driver-1", "driver-2")
        val raceDate = LocalDate.of(2024, 6, 15)
        val trackName = "Monaco Grand Prix Circuit"

        fixture.given()
            .event(RaceCreated(
                raceId = raceId,
                participatingDriverIds = participatingDriverIds,
                raceDate = raceDate,
                trackName = trackName
            ))
            .event(RaceCancelled(raceId = raceId))
            .`when`()
            .command(CancelRace(raceId = raceId))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RaceCancellationResult
                assertThat(payload.success).isFalse()
                assertThat(payload.message).contains("already cancelled")
            }
    }

    @Test
    fun `given race created, when create race command handled, then success result returned`() {
        val raceId = "race-001"
        val participatingDriverIds = listOf("driver-1", "driver-2", "driver-3")
        val raceDate = LocalDate.of(2024, 6, 15)
        val trackName = "Monaco Grand Prix Circuit"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CreateRace(
                raceId = raceId,
                participatingDriverIds = participatingDriverIds,
                raceDate = raceDate,
                trackName = trackName
            ))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RaceCreationResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).contains("successfully")
            }
    }

    @Test
    fun `given race created, when cancel race command handled, then success result returned`() {
        val raceId = "race-001"
        val participatingDriverIds = listOf("driver-1", "driver-2")
        val raceDate = LocalDate.of(2024, 6, 15)
        val trackName = "Monaco Grand Prix Circuit"

        fixture.given()
            .event(RaceCreated(
                raceId = raceId,
                participatingDriverIds = participatingDriverIds,
                raceDate = raceDate,
                trackName = trackName
            ))
            .`when`()
            .command(CancelRace(raceId = raceId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RaceCancellationResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).contains("successfully")
            }
    }
}