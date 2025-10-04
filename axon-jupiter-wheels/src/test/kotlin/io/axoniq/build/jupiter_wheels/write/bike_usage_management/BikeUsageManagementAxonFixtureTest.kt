package io.axoniq.build.jupiter_wheels.write.bike_usage_management

import io.axoniq.build.jupiter_wheels.bike_usage_management.*
import io.axoniq.build.jupiter_wheels.bike_usage_management.api.*
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.mock
import java.time.LocalDateTime

/**
 * Axon Framework test fixture for the Bike Usage Management component.
 * Tests command handling, event sourcing behavior, and component state management.
 */
class BikeUsageManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, BikeUsageManagementState::class.java)
        val commandGateway = mock(CommandGateway::class.java)
        val commandHandlingModule = CommandHandlingModule
            .named("BikeUsageManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> BikeUsageManagementCommandHandler(commandGateway) }

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
    fun `given bike marked as in use, when pause rental, then rental is paused`() {
        val rentalId = "rental-123"
        val bikeId = "bike-456"

        fixture.given()
            .event(BikeMarkedAsInUse(rentalId = rentalId, bikeId = bikeId))
            .`when`()
            .command(PauseRental(rentalId = rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as RentalPaused
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.pauseStartTime).isNotNull()
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RentalPauseResult
                assertThat(payload.pauseConfirmed).isTrue()
            }
    }

    @Test
    fun `given no prior activity, when pause rental, then exception thrown`() {
        val rentalId = "rental-123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(PauseRental(rentalId = rentalId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Rental cannot be paused - current status: UNKNOWN")
            }
    }

    @Test
    fun `given bike marked as in use, when end rental early, then rental ended early`() {
        val rentalId = "rental-123"
        val bikeId = "bike-456"
        val problemDescription = "Flat tire detected"

        fixture.given()
            .event(BikeMarkedAsInUse(rentalId = rentalId, bikeId = bikeId))
            .`when`()
            .command(EndRentalEarlyDueToProblem(problemDescription = problemDescription, rentalId = rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as RentalEndedEarly
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.problemDescription).isEqualTo(problemDescription)
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as EarlyRentalEndResult
                assertThat(payload.rentalEnded).isTrue()
            }
    }

    @Test
    fun `given rental paused, when resume rental, then rental resumed`() {
        val rentalId = "rental-123"
        val bikeId = "bike-456"
        val pauseTime = LocalDateTime.now().minusMinutes(10)

        fixture.given()
            .event(BikeMarkedAsInUse(rentalId = rentalId, bikeId = bikeId))
            .event(RentalPaused(pauseStartTime = pauseTime, rentalId = rentalId))
            .`when`()
            .command(ResumeRental(rentalId = rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as RentalResumed
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.pauseDuration).isGreaterThan(0)
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RentalResumeResult
                assertThat(payload.resumeConfirmed).isTrue()
            }
    }

    @Test
    fun `given bike marked as in use, when resume rental, then exception thrown`() {
        val rentalId = "rental-123"
        val bikeId = "bike-456"

        fixture.given()
            .event(BikeMarkedAsInUse(rentalId = rentalId, bikeId = bikeId))
            .`when`()
            .command(ResumeRental(rentalId = rentalId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Rental cannot be resumed - current status: IN_USE")
            }
    }

    @Test
    fun `given rental paused, when pause timeout deadline, then extra fees charged`() {
        val rentalId = "rental-123"
        val bikeId = "bike-456"
        val pauseTime = LocalDateTime.now().minusMinutes(35)

        fixture.given()
            .event(BikeMarkedAsInUse(rentalId = rentalId, bikeId = bikeId))
            .event(RentalPaused(pauseStartTime = pauseTime, rentalId = rentalId))
            .`when`()
            .command(RentalPauseTimeout(rentalId = rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ExtraFeesChargedExtendedPause
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.extraFee).isEqualTo(5.0)
            }
    }

    @Test
    fun `given rental resumed, when pause timeout deadline, then no events emitted`() {
        val rentalId = "rental-123"
        val bikeId = "bike-456"
        val pauseTime = LocalDateTime.now().minusMinutes(10)

        fixture.given()
            .event(BikeMarkedAsInUse(rentalId = rentalId, bikeId = bikeId))
            .event(RentalPaused(pauseStartTime = pauseTime, rentalId = rentalId))
            .event(RentalResumed(pauseDuration = 10, rentalId = rentalId))
            .`when`()
            .command(RentalPauseTimeout(rentalId = rentalId))
            .then()
            .success()
            .noEvents()
    }

    @Test
    fun `given paused rental, when end rental early, then rental ended early`() {
        val rentalId = "rental-123"
        val bikeId = "bike-456"
        val problemDescription = "Chain broke during pause"
        val pauseTime = LocalDateTime.now().minusMinutes(5)

        fixture.given()
            .event(BikeMarkedAsInUse(rentalId = rentalId, bikeId = bikeId))
            .event(RentalPaused(pauseStartTime = pauseTime, rentalId = rentalId))
            .`when`()
            .command(EndRentalEarlyDueToProblem(problemDescription = problemDescription, rentalId = rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as RentalEndedEarly
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.problemDescription).isEqualTo(problemDescription)
            }
    }
}