package io.axoniq.build.sleep_on_time.write.container_access

import io.axoniq.build.sleep_on_time.container_access.*
import io.axoniq.build.sleep_on_time.container_access.api.*
import io.axoniq.build.sleep_on_time.container_access.exception.ContainerAlreadyOccupied
import io.axoniq.build.sleep_on_time.container_access.exception.GuestAlreadyCheckedOut
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ContainerAccessAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(OpenContainerDoor.TargetIdentifier::class.java, ContainerAccessState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("ContainerAccess")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> ContainerAccessCommandHandler() }

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
    fun `given no prior activity, when open container door, then door opening requested`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(OpenContainerDoor(bookingId, guestId, 42, containerId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as DoorOpeningRequested
                assertThat(event.bookingId).isEqualTo(bookingId)
                assertThat(event.guestId).isEqualTo(guestId)
                assertThat(event.containerId).isEqualTo(containerId)
                assertThat(event.timestamp).isNotNull()
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ContainerDoorOpenResult
                assertThat(payload.success).isTrue()
                assertThat(payload.unlockRequested).isTrue()
            }
    }

    @Test
    fun `given guest already checked out, when open container door, then exception`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"

        fixture.given()
            .event(GuestCheckedOut(bookingId, guestId, LocalDateTime.now(), containerId))
            .`when`()
            .command(OpenContainerDoor(bookingId, guestId, 42, containerId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(GuestAlreadyCheckedOut::class.java)
                    .hasMessageContaining("Guest $guestId has already checked out from container $containerId")
            }
    }

    @Test
    fun `given no prior activity, when confirm door unlocked, then guest checked in`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"
        val unlockTimestamp = LocalDateTime.now()

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ConfirmDoorUnlocked(bookingId, guestId, unlockTimestamp, containerId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as GuestCheckedIn
                assertThat(event.bookingId).isEqualTo(bookingId)
                assertThat(event.guestId).isEqualTo(guestId)
                assertThat(event.containerId).isEqualTo(containerId)
                assertThat(event.checkedInAt).isEqualTo(unlockTimestamp)
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as DoorUnlockConfirmationResult
                assertThat(payload.success).isTrue()
            }
    }

    @Test
    fun `given no prior activity, when obtain container, then container obtained`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ObtainContainer(bookingId, guestId, containerId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ContainerObtained
                assertThat(event.bookingId).isEqualTo(bookingId)
                assertThat(event.guestId).isEqualTo(guestId)
                assertThat(event.containerId).isEqualTo(containerId)
                assertThat(event.timestamp).isNotNull()
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ContainerObtainResult
                assertThat(payload.success).isTrue()
                assertThat(payload.accessKeyRequested).isTrue()
            }
    }

    @Test
    fun `given container already occupied, when obtain container, then exception`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"

        fixture.given()
            .event(GuestCheckedIn(LocalDateTime.now(), bookingId, guestId, containerId))
            .`when`()
            .command(ObtainContainer(bookingId, guestId, containerId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(ContainerAlreadyOccupied::class.java)
                    .hasMessageContaining("Container $containerId is already occupied or reserved")
            }
    }

    @Test
    fun `given container already reserved, when obtain container, then exception`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"

        fixture.given()
            .event(ContainerObtained(bookingId, guestId, LocalDateTime.now(), containerId))
            .`when`()
            .command(ObtainContainer(bookingId, guestId, containerId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(ContainerAlreadyOccupied::class.java)
                    .hasMessageContaining("Container $containerId is already occupied or reserved")
            }
    }

    @Test
    fun `given guest checked in, when open container door, then door opening requested`() {
        val bookingId = "booking-123"
        val guestId = "guest-456"
        val containerId = "container-789"

        fixture.given()
            .event(GuestCheckedIn(LocalDateTime.now(), bookingId, guestId, containerId))
            .`when`()
            .command(OpenContainerDoor(bookingId, guestId, 42, containerId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as DoorOpeningRequested
                assertThat(event.bookingId).isEqualTo(bookingId)
                assertThat(event.guestId).isEqualTo(guestId)
                assertThat(event.containerId).isEqualTo(containerId)
            }
    }
}