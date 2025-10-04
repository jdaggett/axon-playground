package io.axoniq.build.jupiter_wheels.write.emergency_support_management

import io.axoniq.build.jupiter_wheels.emergency_support_management.*
import io.axoniq.build.jupiter_wheels.emergency_support_management.api.*
import org.axonframework.test.fixture.AxonTestFixture
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

/**
 * EmergencySupportManagementAxonFixtureTest - Tests for Emergency Support Management command handling
 * Verifies the behavior of emergency support commands and their resulting events
 */
class EmergencySupportManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture
    
    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, EmergencySupportManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("EmergencySupportManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> EmergencySupportManagementCommandHandler() }
         
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
    fun `given no prior activity, when contact emergency support, then emergency support contacted event published`() {
        val rentalId = "rental-123"
        val emergencyType = "bike-breakdown"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ContactEmergencySupport(emergencyType = emergencyType, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(EmergencySupportResult::class.java)
                val supportResult = result.payload() as EmergencySupportResult
                assertThat(supportResult.supportContactId).isNotNull()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as EmergencySupportContacted
                assertThat(event.emergencyType).isEqualTo(emergencyType)
                assertThat(event.rentalId).isEqualTo(rentalId)
            }
    }

    @Test
    fun `given emergency support already contacted, when contact emergency support again, then new event published with warning`() {
        val rentalId = "rental-456"
        val emergencyType = "accident"

        fixture.given()
            .event(EmergencySupportContacted(emergencyType = "breakdown", rentalId = rentalId))
            .`when`()
            .command(ContactEmergencySupport(emergencyType = emergencyType, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(EmergencySupportResult::class.java)
                val supportResult = result.payload() as EmergencySupportResult
                assertThat(supportResult.supportContactId).isNotNull()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as EmergencySupportContacted
                assertThat(event.emergencyType).isEqualTo(emergencyType)
                assertThat(event.rentalId).isEqualTo(rentalId)
            }
    }

    @Test
    fun `given no prior activity, when provide valid GPS location, then support connection established`() {
        val rentalId = "rental-789"
        val latitude = 52.3676
        val longitude = 4.9041

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ProvideGPSLocation(latitude = latitude, longitude = longitude, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(GPSLocationResult::class.java)
                val locationResult = result.payload() as GPSLocationResult
                assertThat(locationResult.locationProvided).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as SupportConnectionEstablished
                assertThat(event.supportAgentId).isNotNull()
                assertThat(event.rentalId).isEqualTo(rentalId)
            }
    }

    @Test
    fun `given invalid latitude, when provide GPS location, then location not provided and no events`() {
        val rentalId = "rental-invalid"
        val invalidLatitude = 95.0 // Invalid latitude (> 90)
        val longitude = 4.9041

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ProvideGPSLocation(latitude = invalidLatitude, longitude = longitude, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(GPSLocationResult::class.java)
                val locationResult = result.payload() as GPSLocationResult
                assertThat(locationResult.locationProvided).isFalse()
            }
            .noEvents()
    }

    @Test
    fun `given invalid longitude, when provide GPS location, then location not provided and no events`() {
        val rentalId = "rental-invalid-lng"
        val latitude = 52.3676
        val invalidLongitude = -185.0 // Invalid longitude (< -180)

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ProvideGPSLocation(latitude = latitude, longitude = invalidLongitude, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                assertThat(result.payload())
                    .isInstanceOf(GPSLocationResult::class.java)
                val locationResult = result.payload() as GPSLocationResult
                assertThat(locationResult.locationProvided).isFalse()
            }
            .noEvents()
    }

    @Test
    fun `given payment completed, when contact emergency support, then emergency support contacted and state updated`() {
        val rentalId = "rental-payment-completed"
        val paymentId = "payment-123"
        val emergencyType = "theft"

        fixture.given()
            .event(PaymentCompleted(paymentId = paymentId, rentalId = rentalId))
            .`when`()
            .command(ContactEmergencySupport(emergencyType = emergencyType, rentalId = rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as EmergencySupportContacted
                assertThat(event.emergencyType).isEqualTo(emergencyType)
                assertThat(event.rentalId).isEqualTo(rentalId)
            }
    }

    @Test
    fun `given support connection established, when provide GPS location again, then new connection established`() {
        val rentalId = "rental-reconnect"
        val latitude = 51.5074
        val longitude = -0.1278

        fixture.given()
            .event(SupportConnectionEstablished(supportAgentId = "agent-1", rentalId = rentalId))
            .`when`()
            .command(ProvideGPSLocation(latitude = latitude, longitude = longitude, rentalId = rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as SupportConnectionEstablished
                assertThat(event.supportAgentId).isNotNull()
                assertThat(event.rentalId).isEqualTo(rentalId)
            }
    }
}