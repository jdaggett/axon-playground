package io.axoniq.build.sleep_on_time.write.issue_reporting

import io.axoniq.build.sleep_on_time.issue_reporting.IssueReportingCommandHandler
import io.axoniq.build.sleep_on_time.issue_reporting.IssueReportingState
import io.axoniq.build.sleep_on_time.issue_reporting.api.ReportContainerIssue
import io.axoniq.build.sleep_on_time.issue_reporting.api.IssueReportResult
import io.axoniq.build.sleep_on_time.issue_reporting.api.ContainerIssueReported
import io.axoniq.build.sleep_on_time.issue_reporting.exception.InvalidIssueData
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
 * Axon Framework 5 fixture tests for the Issue Reporting Service component.
 * Tests command handling, event sourcing, and exception scenarios.
 */
class IssueReportingAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(ReportContainerIssue.TargetIdentifier::class.java, IssueReportingState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("IssueReporting")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> IssueReportingCommandHandler() }

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
    fun `given no prior activity, when reporting valid container issue, then issue reported successfully`() {
        val command = ReportContainerIssue(
            bookingId = "booking-123",
            issueType = "CLEANLINESS",
            guestId = "guest-456",
            description = "The container needs cleaning",
            severity = "MEDIUM",
            containerId = "container-789"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as IssueReportResult
                assertThat(payload.success).isTrue()
                assertThat(payload.issueId).isNotBlank()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ContainerIssueReported
                assertThat(event.bookingId).isEqualTo("booking-123")
                assertThat(event.issueType).isEqualTo("CLEANLINESS")
                assertThat(event.guestId).isEqualTo("guest-456")
                assertThat(event.description).isEqualTo("The container needs cleaning")
                assertThat(event.severity).isEqualTo("MEDIUM")
                assertThat(event.containerId).isEqualTo("container-789")
                assertThat(event.issueId).isNotBlank()
                assertThat(event.reportedAt).isNotNull()
            }
    }

    @Test
    fun `given issue already reported, when reporting same issue again, then exception thrown`() {
        val existingEvent = ContainerIssueReported(
            issueId = "issue-123",
            bookingId = "booking-123",
            issueType = "SAFETY",
            guestId = "guest-456",
            description = "Previous issue",
            reportedAt = java.time.LocalDateTime.now(),
            severity = "HIGH",
            containerId = "container-789"
        )

        val command = ReportContainerIssue(
            bookingId = "booking-123",
            issueType = "MAINTENANCE",
            guestId = "guest-456",
            description = "Another issue",
            severity = "LOW",
            containerId = "container-789"
        )

        fixture.given()
            .event(existingEvent)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(InvalidIssueData::class.java)
                    .hasMessageContaining("Issue has already been reported for this booking")
            }
    }

    @Test
    fun `given no prior activity, when reporting issue with empty description, then exception thrown`() {
        val command = ReportContainerIssue(
            bookingId = "booking-123",
            issueType = "CLEANLINESS",
            guestId = "guest-456",
            description = "",
            severity = "MEDIUM",
            containerId = "container-789"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(InvalidIssueData::class.java)
                    .hasMessageContaining("Issue description cannot be empty")
            }
    }

    @Test
    fun `given no prior activity, when reporting issue with invalid severity, then exception thrown`() {
        val command = ReportContainerIssue(
            bookingId = "booking-123",
            issueType = "CLEANLINESS",
            guestId = "guest-456",
            description = "Valid description",
            severity = "INVALID_SEVERITY",
            containerId = "container-789"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(InvalidIssueData::class.java)
                    .hasMessageContaining("Invalid severity level")
            }
    }

    @Test
    fun `given no prior activity, when reporting issue with invalid issue type, then exception thrown`() {
        val command = ReportContainerIssue(
            bookingId = "booking-123",
            issueType = "INVALID_TYPE",
            guestId = "guest-456",
            description = "Valid description",
            severity = "HIGH",
            containerId = "container-789"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(InvalidIssueData::class.java)
                    .hasMessageContaining("Invalid issue type")
            }
    }

    @Test
    fun `given no prior activity, when reporting critical safety issue, then issue reported with correct severity`() {
        val command = ReportContainerIssue(
            bookingId = "booking-456",
            issueType = "SAFETY",
            guestId = "guest-789",
            description = "Dangerous electrical hazard in container",
            severity = "CRITICAL",
            containerId = "container-123"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ContainerIssueReported
                assertThat(event.issueType).isEqualTo("SAFETY")
                assertThat(event.severity).isEqualTo("CRITICAL")
                assertThat(event.description).isEqualTo("Dangerous electrical hazard in container")
            }
    }
}