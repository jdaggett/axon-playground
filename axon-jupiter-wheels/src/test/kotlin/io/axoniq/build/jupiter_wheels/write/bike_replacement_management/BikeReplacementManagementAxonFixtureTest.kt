package io.axoniq.build.jupiter_wheels.write.bike_replacement_management

import io.axoniq.build.jupiter_wheels.bike_replacement_management.*
import io.axoniq.build.jupiter_wheels.bike_replacement_management.api.*
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
 * Test class for the Bike Replacement Management component using Axon Test Fixture.
 * Tests command handling and event sourcing functionality.
 */
class BikeReplacementManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture
    
    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, BikeReplacementManagementState::class.java)
        
        val commandHandlingModule = CommandHandlingModule
            .named("BikeReplacementManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> BikeReplacementManagementCommandHandler() }

        configurer = configurer.registerEntity(stateEntity)
            .registerCommandHandlingModule(commandHandlingModule)
            .componentRegistry { cr -> cr.disableEnhancer(AxonServerConfigurationEnhancer::class.java) }
        fixture = AxonTestFixture.with(configurer)
    }

    @AfterEach
    fun afterEach() {
        fixture.stop()
    }

    /**
     * Tests that requesting a bike replacement successfully creates a replacement request.
     */
    @Test
    fun `given no prior activity, when request bike replacement, then bike replacement requested`() {
        val originalBikeId = "bike-123"
        val rentalId = "rental-456"
        val issueDescription = "Flat tire"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RequestBikeReplacement(
                originalBikeId = originalBikeId,
                rentalId = rentalId,
                issueDescription = issueDescription
            ))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as BikeReplacementRequested
                assertThat(event.originalBikeId).isEqualTo(originalBikeId)
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.issueDescription).isEqualTo(issueDescription)
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as BikeReplacementRequestResult
                assertThat(payload.replacementRequestId).isNotNull()
            }
    }

    /**
     * Tests that assigning a replacement bike after a request is successful.
     */
    @Test
    fun `given bike replacement requested, when assign replacement bike, then replacement bike assigned`() {
        val originalBikeId = "bike-123"
        val rentalId = "rental-456"
        val replacementBikeId = "bike-789"

        fixture.given()
            .event(BikeReplacementRequested(
                originalBikeId = originalBikeId,
                rentalId = rentalId,
                issueDescription = "Flat tire"
            ))
            .`when`()
            .command(AssignReplacementBike(
                replacementBikeId = replacementBikeId,
                rentalId = rentalId
            ))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ReplacementBikeAssigned
                assertThat(event.originalBikeId).isEqualTo(originalBikeId)
                assertThat(event.replacementBikeId).isEqualTo(replacementBikeId)
                assertThat(event.rentalId).isEqualTo(rentalId)
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as BikeReplacementAssignmentResult
                assertThat(payload.assignmentConfirmed).isTrue()
            }
    }

    /**
     * Tests that assigning a replacement bike without a prior request fails.
     */
    @Test
    fun `given no bike replacement requested, when assign replacement bike, then exception`() {
        val rentalId = "rental-456"
        val replacementBikeId = "bike-789"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(AssignReplacementBike(
                replacementBikeId = replacementBikeId,
                rentalId = rentalId
            ))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("No bike replacement request found for this rental")
            }
    }

    /**
     * Tests that multiple replacement requests for the same rental work correctly.
     */
    @Test
    fun `given bike replacement requested and assigned, when request another replacement, then new replacement requested`() {
        val originalBikeId = "bike-123"
        val rentalId = "rental-456"
        val firstReplacementBikeId = "bike-789"
        val newIssueDescription = "Chain broken"

        fixture.given()
            .event(BikeReplacementRequested(
                originalBikeId = originalBikeId,
                rentalId = rentalId,
                issueDescription = "Flat tire"
            ))
            .event(ReplacementBikeAssigned(
                originalBikeId = originalBikeId,
                replacementBikeId = firstReplacementBikeId,
                rentalId = rentalId
            ))
            .`when`()
            .command(RequestBikeReplacement(
                originalBikeId = originalBikeId,
                rentalId = rentalId,
                issueDescription = newIssueDescription
            ))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as BikeReplacementRequested
                assertThat(event.originalBikeId).isEqualTo(originalBikeId)
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.issueDescription).isEqualTo(newIssueDescription)
            }
    }
}