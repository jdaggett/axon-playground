package io.axoniq.build.sleep_on_time.write.guest_checkout_service

import io.axoniq.build.sleep_on_time.guest_checkout_service.GuestCheckoutServiceCommandHandler
import io.axoniq.build.sleep_on_time.guest_checkout_service.GuestCheckoutServiceState
import io.axoniq.build.sleep_on_time.guest_checkout_service.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Test class for the Guest Checkout Service component using Axon Framework test fixture.
 * Tests command handling scenarios for guest checkout operations.
 */
class GuestCheckoutServiceAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(InitiateCheckOut.TargetIdentifier::class.java, GuestCheckoutServiceState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("GuestCheckoutService")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> GuestCheckoutServiceCommandHandler() }

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
    fun `given guest not checked in, when initiate checkout, then checkout fails`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(InitiateCheckOut(bookingId, guestId, containerId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val checkoutResult = result.payload() as CheckOutResult
                assertThat(checkoutResult.success).isFalse()
                assertThat(checkoutResult.accessKeyWithdrawn).isFalse()
            }
            .noEvents()
    }

    @Test
    fun `given guest checked in, when initiate checkout, then checkout succeeds`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"
        val checkinTime = LocalDateTime.of(2024, 1, 15, 14, 30)

        fixture.given()
            .event(GuestCheckedIn(checkinTime, bookingId, guestId, containerId))
            .`when`()
            .command(InitiateCheckOut(bookingId, guestId, containerId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val checkoutResult = result.payload() as CheckOutResult
                assertThat(checkoutResult.success).isTrue()
                assertThat(checkoutResult.accessKeyWithdrawn).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as GuestCheckedOut
                assertThat(event.bookingId).isEqualTo(bookingId)
                assertThat(event.guestId).isEqualTo(guestId)
                assertThat(event.containerId).isEqualTo(containerId)
                assertThat(event.timestamp).isNotNull()
            }
    }

    @Test
    fun `given guest checked in and container obtained, when initiate checkout, then checkout succeeds`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"
        val checkinTime = LocalDateTime.of(2024, 1, 15, 14, 30)
        val obtainedTime = LocalDateTime.of(2024, 1, 15, 15, 0)

        fixture.given()
            .event(GuestCheckedIn(checkinTime, bookingId, guestId, containerId))
            .event(ContainerObtained(bookingId, guestId, obtainedTime, containerId))
            .`when`()
            .command(InitiateCheckOut(bookingId, guestId, containerId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val checkoutResult = result.payload() as CheckOutResult
                assertThat(checkoutResult.success).isTrue()
                assertThat(checkoutResult.accessKeyWithdrawn).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as GuestCheckedOut
                assertThat(event.bookingId).isEqualTo(bookingId)
                assertThat(event.guestId).isEqualTo(guestId)
                assertThat(event.containerId).isEqualTo(containerId)
                assertThat(event.timestamp).isNotNull()
            }
    }

    @Test
    fun `given guest already checked out, when initiate checkout again, then checkout fails`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"
        val checkinTime = LocalDateTime.of(2024, 1, 15, 14, 30)
        val checkoutTime = LocalDateTime.of(2024, 1, 15, 16, 0)

        fixture.given()
            .event(GuestCheckedIn(checkinTime, bookingId, guestId, containerId))
            .event(GuestCheckedOut(bookingId, guestId, checkoutTime, containerId))
            .`when`()
            .command(InitiateCheckOut(bookingId, guestId, containerId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val checkoutResult = result.payload() as CheckOutResult
                assertThat(checkoutResult.success).isFalse()
                assertThat(checkoutResult.accessKeyWithdrawn).isFalse()
            }
            .noEvents()
    }
}