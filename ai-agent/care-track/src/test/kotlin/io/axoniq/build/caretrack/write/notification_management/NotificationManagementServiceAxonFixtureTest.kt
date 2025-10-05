package io.axoniq.build.caretrack.write.notification_management

import io.axoniq.build.caretrack.notification_management.NotificationManagementServiceCommandHandler
import io.axoniq.build.caretrack.notification_management.NotificationManagementServiceState
import io.axoniq.build.caretrack.notification_management.api.*
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
 * Test class for the Notification Management Service component using AxonTestFixture.
 * Tests command handling, event sourcing, and state evolution for notifications and alerts.
 */
class NotificationManagementServiceAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture
    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, NotificationManagementServiceState::class.java)
        
        val commandHandlingModule = CommandHandlingModule
            .named("NotificationManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> NotificationManagementServiceCommandHandler() }

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
    fun `given no prior activity, when create urgent health notification, then notification created event`() {
        val patientId = "patient-123"
        val message = "Critical blood pressure reading detected"
        val priority = "HIGH"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CreateUrgentHealthNotification(
                message = message,
                patientId = patientId,
                priority = priority
            ))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as UrgentNotificationResult
                assertThat(payload.notificationCreated).isTrue()
                assertThat(payload.notificationId).isNotBlank()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as UrgentHealthNotificationCreated
                assertThat(event.patientId).isEqualTo(patientId)
                assertThat(event.message).isEqualTo(message)
                assertThat(event.priority).isEqualTo(priority)
                assertThat(event.notificationId).isNotBlank()
            }
    }

    @Test
    fun `given no prior activity, when create missed appointment alert, then alert created event`() {
        val patientId = "patient-456"
        val appointmentId = "appt-789"
        val alertMessage = "Patient missed scheduled appointment"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CreateMissedAppointmentAlert(
                patientId = patientId,
                appointmentId = appointmentId,
                alertMessage = alertMessage
            ))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as MissedAppointmentAlertResult
                assertThat(payload.alertCreated).isTrue()
                assertThat(payload.alertId).isNotBlank()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as MissedAppointmentAlertCreated
                assertThat(event.patientId).isEqualTo(patientId)
                assertThat(event.appointmentId).isEqualTo(appointmentId)
                assertThat(event.alertMessage).isEqualTo(alertMessage)
                assertThat(event.alertId).isNotBlank()
            }
    }

    @Test
    fun `given no prior activity, when create treatment notification, then notification created event`() {
        val patientId = "patient-789"
        val treatmentDetails = "New medication prescribed: Lisinopril 10mg daily"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CreateTreatmentNotification(
                patientId = patientId,
                treatmentDetails = treatmentDetails
            ))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TreatmentNotificationResult
                assertThat(payload.notificationCreated).isTrue()
                assertThat(payload.notificationId).isNotBlank()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as TreatmentNotificationCreated
                assertThat(event.patientId).isEqualTo(patientId)
                assertThat(event.treatmentDetails).isEqualTo(treatmentDetails)
                assertThat(event.notificationId).isNotBlank()
            }
    }

    @Test
    fun `given urgent notification exists, when acknowledge notification, then acknowledgment event`() {
        val notificationId = "notif-123"
        val patientId = "patient-123"
        val familyMemberEmail = "family@example.com"
        
        fixture.given()
            .event(UrgentHealthNotificationCreated(
                patientId = patientId,
                message = "Critical health alert",
                notificationId = notificationId,
                priority = "HIGH"
            ))
            .`when`()
            .command(AcknowledgeUrgentNotification(
                familyMemberEmail = familyMemberEmail,
                notificationId = notificationId
            ))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as NotificationAcknowledgmentResult
                assertThat(payload.acknowledged).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as UrgentNotificationAcknowledged
                assertThat(event.notificationId).isEqualTo(notificationId)
                assertThat(event.familyMemberEmail).isEqualTo(familyMemberEmail)
            }
    }

    @Test
    fun `given multiple notifications, when acknowledge one, then only that notification acknowledged`() {
        val notificationId1 = "notif-123"
        val notificationId2 = "notif-456"
        val patientId = "patient-123"
        val familyMemberEmail = "family@example.com"
        
        fixture.given()
            .event(UrgentHealthNotificationCreated(
                patientId = patientId,
                message = "First critical alert",
                notificationId = notificationId1,
                priority = "HIGH"
            ))
            .event(UrgentHealthNotificationCreated(
                patientId = patientId,
                message = "Second critical alert",
                notificationId = notificationId2,
                priority = "HIGH"
            ))
            .`when`()
            .command(AcknowledgeUrgentNotification(
                familyMemberEmail = familyMemberEmail,
                notificationId = notificationId1
            ))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as UrgentNotificationAcknowledged
                assertThat(event.notificationId).isEqualTo(notificationId1)
                assertThat(event.familyMemberEmail).isEqualTo(familyMemberEmail)
            }
    }
}