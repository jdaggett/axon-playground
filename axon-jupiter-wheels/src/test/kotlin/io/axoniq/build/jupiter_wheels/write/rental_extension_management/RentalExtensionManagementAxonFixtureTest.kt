package io.axoniq.build.jupiter_wheels.write.rental_extension_management

import io.axoniq.build.jupiter_wheels.rental_extension_management.*
import io.axoniq.build.jupiter_wheels.rental_extension_management.api.*
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
 * Test class for Rental Extension Management component using AxonTestFixture.
 * Tests command handling, event sourcing and deadline processing functionality.
 */
class RentalExtensionManagementAxonFixtureTest {
    
    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, RentalExtensionManagementState::class.java)
        
        val commandHandlingModule = CommandHandlingModule
            .named("RentalExtensionManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> RentalExtensionManagementCommandHandler(c.getComponent(org.axonframework.commandhandling.gateway.CommandGateway::class.java)) }
        
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
    fun `given no prior activity, when requesting rental extension, then extension requested event is published`() {
        val rentalId = "rental-123"
        val additionalTime = 30

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RequestRentalExtension(additionalTime, rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as RentalExtensionRequested
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.additionalTime).isEqualTo(additionalTime)
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RentalExtensionRequestResult
                assertThat(payload.extensionRequestId).startsWith("ext-$rentalId-")
            }
    }

    @Test
    fun `given extension requested, when approving rental extension, then extension approved event is published`() {
        val rentalId = "rental-456"
        val approvedTime = 45

        fixture.given()
            .event(RentalExtensionRequested(30, rentalId))
            .`when`()
            .command(ApproveRentalExtension(approvedTime, rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as RentalExtensionApproved
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.approvedTime).isEqualTo(approvedTime)
                assertThat(event.newEndTime).isNotNull()
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RentalExtensionApprovalResult
                assertThat(payload.approvalConfirmed).isTrue()
            }
    }
    
    @Test
    fun `given no extension request, when approving rental extension, then approval not confirmed`() {
        val rentalId = "rental-789"
        val approvedTime = 45

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ApproveRentalExtension(approvedTime, rentalId))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as RentalExtensionApprovalResult
                assertThat(payload.approvalConfirmed).isFalse()
            }
    }

    @Test
    fun `given extension requested but not grace period active, when processing timeout, then grace period activated`() {
        val rentalId = "rental-timeout"

        fixture.given()
            .event(RentalExtensionRequested(30, rentalId))
            .`when`()
            .command(RentalExtensionProcessingTimeout(rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as GracePeriodActivated
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.gracePeriodMinutes).isEqualTo(15)
            }
    }
    
    @Test
    fun `given grace period already active, when processing timeout, then no additional events`() {
        val rentalId = "rental-grace"

        fixture.given()
            .event(RentalExtensionRequested(30, rentalId))
            .event(GracePeriodActivated(15, rentalId))
            .`when`()
            .command(RentalExtensionProcessingTimeout(rentalId))
            .then()
            .success()
            .noEvents()
    }

    @Test
    fun `given no extension request, when processing timeout, then no events published`() {
        val rentalId = "rental-no-request"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RentalExtensionProcessingTimeout(rentalId))
            .then()
            .success()
            .noEvents()
    }
}