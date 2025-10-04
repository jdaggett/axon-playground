package io.axoniq.build.jupiter_wheels.write.bike_fleet_management

import io.axoniq.build.jupiter_wheels.bike_fleet_management.BikeFleetManagementCommandHandler
import io.axoniq.build.jupiter_wheels.bike_fleet_management.BikeFleetManagementState
import io.axoniq.build.jupiter_wheels.bike_fleet_management.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Test class for Bike Fleet Management component using Axon Framework 5 test fixture.
 * Tests command handling, event sourcing, and exception scenarios.
 */
class BikeFleetManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, BikeFleetManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("BikeFleetManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> BikeFleetManagementCommandHandler() }

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
    fun `given no prior activity, when create new bike, then bike created and marked as available`() {
        val command = CreateNewBike(
            location = "Downtown Station",
            bikeType = "Electric",
            condition = "Good"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val bikeCreationResult = result.payload() as BikeCreationResult
                assertThat(bikeCreationResult.bikeId).isNotNull()}
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)

                val bikeCreatedEvent = events[0] as BikeCreated
                assertThat(bikeCreatedEvent.location).isEqualTo("Downtown Station")
                assertThat(bikeCreatedEvent.bikeType).isEqualTo("Electric")
                assertThat(bikeCreatedEvent.condition).isEqualTo("Good")
                assertThat(bikeCreatedEvent.bikeId).isNotNull()

                val bikeMarkedAsAvailableEvent = events[1] as BikeMarkedAsAvailable
                assertThat(bikeMarkedAsAvailableEvent.bikeId).isEqualTo(bikeCreatedEvent.bikeId)
            }
    }

    @Test
    fun `given bike created, when remove bike from fleet, then bike removed from fleet`() {
        val bikeId = "test-bike-id"
        val bikeCreatedEvent = BikeCreated(
            bikeId = bikeId,
            location = "Downtown Station",
            bikeType = "Electric",
            condition = "Good"
        )
        val bikeMarkedAsAvailableEvent = BikeMarkedAsAvailable(bikeId = bikeId)

        val command = RemoveBikeFromFleet(
            bikeId = bikeId,
            removalReason = "Maintenance required"
        )

        fixture.given()
            .event(bikeCreatedEvent)
            .event(bikeMarkedAsAvailableEvent)
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val bikeRemovalResult = result.payload() as BikeRemovalResult
                assertThat(bikeRemovalResult.removalConfirmed).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val bikeRemovedEvent = events[0] as BikeRemovedFromFleet
                assertThat(bikeRemovedEvent.bikeId).isEqualTo(bikeId)
                assertThat(bikeRemovedEvent.removalReason).isEqualTo("Maintenance required")
            }
    }

    @Test
    fun `given no bike exists, when remove bike from fleet, then exception thrown`() {
        val command = RemoveBikeFromFleet(
            bikeId = "non-existent-bike-id",
            removalReason = "Maintenance required"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Bike with given id does not exist")
            }
    }

    @Test
    fun `given bike already removed, when remove bike from fleet again, then exception thrown`() {
        val bikeId = "test-bike-id"
        val bikeCreatedEvent = BikeCreated(
            bikeId = bikeId,
            location = "Downtown Station",
            bikeType = "Electric",
            condition = "Good"
        )
        val bikeMarkedAsAvailableEvent = BikeMarkedAsAvailable(bikeId = bikeId)
        val bikeRemovedEvent = BikeRemovedFromFleet(
            bikeId = bikeId,
            removalReason = "Previous maintenance"
        )

        val command = RemoveBikeFromFleet(
            bikeId = bikeId,
            removalReason = "Another maintenance"
        )

        fixture.given()
            .event(bikeCreatedEvent)
            .event(bikeMarkedAsAvailableEvent)
            .event(bikeRemovedEvent)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Bike is already removed from fleet")
            }
    }

    @Test
    fun `given bike created with different conditions, when create multiple bikes, then all bikes created successfully`() {
        val command1 = CreateNewBike(
            location = "North Station",
            bikeType = "Mountain",
            condition = "Excellent"
        )

        val command2 = CreateNewBike(
            location = "South Station",
            bikeType = "Road",
            condition = "Fair"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command1)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)
                val bikeCreatedEvent = events[0] as BikeCreated
                assertThat(bikeCreatedEvent.location).isEqualTo("North Station")
                assertThat(bikeCreatedEvent.bikeType).isEqualTo("Mountain")
                assertThat(bikeCreatedEvent.condition).isEqualTo("Excellent")
            }

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command2)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)
                val bikeCreatedEvent = events[0] as BikeCreated
                assertThat(bikeCreatedEvent.location).isEqualTo("South Station")
                assertThat(bikeCreatedEvent.bikeType).isEqualTo("Road")
                assertThat(bikeCreatedEvent.condition).isEqualTo("Fair")
            }
    }
}