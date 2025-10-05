package io.axoniq.build.apex_racing_labs.write.driver_management

import io.axoniq.build.apex_racing_labs.driver_management.*
import io.axoniq.build.apex_racing_labs.driver_management.api.*
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

/**
 * Test class for Driver Management Service using Axon Test Fixture.
 * Tests command handling, event sourcing, and exception scenarios.
 */
class DriverManagementServiceAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, DriverManagementServiceState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("DriverManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> DriverManagementServiceCommandHandler() }

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
    fun `given no driver exists, when creating driver, then driver created successfully`() {
        val driverId = "driver-001"
        val teamId = "team-001"
        val driverName = "Lewis Hamilton"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CreateDriver(teamId, driverId, driverName))
            .then()
            .success()
            .events(DriverCreated(teamId, driverId, driverName))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as DriverCreationResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Driver created successfully")
            }
    }

    @Test
    fun `given driver already exists, when creating same driver, then creation fails`() {
        val driverId = "driver-001"
        val teamId = "team-001"
        val driverName = "Lewis Hamilton"

        fixture.given()
            .event(DriverCreated(teamId, driverId, driverName))
            .`when`()
            .command(CreateDriver(teamId, driverId, driverName))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as DriverCreationResult
                assertThat(payload.success).isFalse()
                assertThat(payload.message).isEqualTo("Driver already exists")
            }
    }

    @Test
    fun `given driver exists, when removing driver, then driver removed successfully`() {
        val driverId = "driver-001"
        val teamId = "team-001"
        val driverName = "Lewis Hamilton"

        fixture.given()
            .event(DriverCreated(teamId, driverId, driverName))
            .`when`()
            .command(RemoveDriver(driverId))
            .then()
            .success()
            .events(DriverRemoved(driverId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as DriverRemovalResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Driver removed successfully")
            }
    }
    
    @Test
    fun `given no driver exists, when removing driver, then removal fails`() {
        val driverId = "driver-001"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RemoveDriver(driverId))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as DriverRemovalResult
                assertThat(payload.success).isFalse()
                assertThat(payload.message).isEqualTo("Driver does not exist")
            }
    }

    @Test
    fun `given driver was removed, when removing same driver again, then removal fails`() {
        val driverId = "driver-001"
        val teamId = "team-001"
        val driverName = "Lewis Hamilton"

        fixture.given()
            .event(DriverCreated(teamId, driverId, driverName))
            .event(DriverRemoved(driverId))
            .`when`()
            .command(RemoveDriver(driverId))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as DriverRemovalResult
                assertThat(payload.success).isFalse()
                assertThat(payload.message).isEqualTo("Driver does not exist")
            }
    }

    @Test
    fun `given driver was removed, when creating driver with same id, then driver created successfully`() {
        val driverId = "driver-001"
        val teamId = "team-001"
        val driverName = "Lewis Hamilton"
        val newDriverName = "Max Verstappen"

        fixture.given()
            .event(DriverCreated(teamId, driverId, driverName))
            .event(DriverRemoved(driverId))
            .`when`()
            .command(CreateDriver(teamId, driverId, newDriverName))
            .then()
            .success()
            .events(DriverCreated(teamId, driverId, newDriverName))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as DriverCreationResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Driver created successfully")
            }
    }
}