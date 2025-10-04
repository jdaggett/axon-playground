package io.axoniq.build.jupiter_wheels.write.bike_rental_management

import io.axoniq.build.jupiter_wheels.bike_rental_management.*
import io.axoniq.build.jupiter_wheels.bike_rental_management.api.*
import io.axoniq.build.jupiter_wheels.bike_rental_management.exception.BikeAlreadyReserved
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Test class for Bike Rental Management component using Axon Framework fixture
 */
class BikeRentalManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture
    
    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(RequestBikeRental.TargetIdentifier::class.java, BikeRentalManagementState::class.java)
        
        val commandHandlingModule = CommandHandlingModule
            .named("BikeRentalManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> 
                BikeRentalManagementCommandHandler(c.getComponent(CommandGateway::class.java))
            }
        
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
    fun `given available bike, when request bike rental, then bike rental requested`() {
        val bikeId = "bike123"
        val userId = "user456"

        fixture.given()
            .event(BikeCreated("Downtown Station", "Electric", "Good", bikeId))
            .`when`()
            .command(RequestBikeRental(userId, bikeId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as BikeRentalRequested
                assertThat(event.userId).isEqualTo(userId)
                assertThat(event.bikeId).isEqualTo(bikeId)
                assertThat(event.rentalId).isNotNull()
            }
            .resultMessageSatisfies { result ->
                val rentalResult = result.payload() as BikeRentalRequestResult
                assertThat(rentalResult.bikeId).isEqualTo(bikeId)
                assertThat(rentalResult.rentalId).isNotNull()
            }
    }

    @Test
    fun `given bike rental requested, when another user requests same bike, then bike already reserved exception`() {
        val bikeId = "bike123"
        val userId1 = "user456"
        val userId2 = "user789"
        val rentalId = "rental123"

        fixture.given()
            .event(BikeCreated("Downtown Station", "Electric", "Good", bikeId))
            .event(BikeRentalRequested(userId1, rentalId, bikeId))
            .`when`()
            .command(RequestBikeRental(userId2, bikeId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(BikeAlreadyReserved::class.java)
                    .hasMessageContaining("Bike $bikeId is already reserved")
            }
    }
    
    @Test
    fun `given no bike created, when request bike rental, then bike not available exception`() {
        val bikeId = "bike123"
        val userId = "user456"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RequestBikeRental(userId, bikeId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Bike $bikeId is not available")
            }
    }

    @Test
    fun `given bike removed from fleet, when request bike rental, then bike removed exception`() {
        val bikeId = "bike123"
        val userId = "user456"

        fixture.given()
            .event(BikeCreated("Downtown Station", "Electric", "Good", bikeId))
            .event(BikeRemovedFromFleet("Maintenance", bikeId))
            .`when`()
            .command(RequestBikeRental(userId, bikeId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Bike $bikeId has been removed from the fleet")
            }
    }

    @Test
    fun `given rental request rejected due to timeout, when request bike rental again, then success`() {
        val bikeId = "bike123"
        val userId = "user456"
        val oldRentalId = "rental123"

        fixture.given()
            .event(BikeCreated("Downtown Station", "Electric", "Good", bikeId))
            .event(BikeRentalRequested(userId, oldRentalId, bikeId))
            .event(RentalRequestRejectedTimeout(oldRentalId, bikeId))
            .`when`()
            .command(RequestBikeRental(userId, bikeId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as BikeRentalRequested
                assertThat(event.userId).isEqualTo(userId)
                assertThat(event.bikeId).isEqualTo(bikeId)
                assertThat(event.rentalId).isNotEqualTo(oldRentalId)
            }
    }

    @Test
    fun `given payment timeout deadline and active rental, when handle deadline, then rental rejected`() {
        val bikeId = "bike123"
        val userId = "user456"
        val rentalId = "rental123"

        fixture.given()
            .event(BikeCreated("Downtown Station", "Electric", "Good", bikeId))
            .event(BikeRentalRequested(userId, rentalId, bikeId))
            .`when`()
            .command(PaymentTimeout(rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as RentalRequestRejectedTimeout
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.bikeId).isEqualTo(bikeId)
            }
    }
    
    @Test
    fun `given payment timeout deadline and no active rental, when handle deadline, then no events`() {
        val rentalId = "rental123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(PaymentTimeout(rentalId))
            .then()
            .success()
            .noEvents()
    }
}