package io.axoniq.build.sleep_on_time.issue_reporting

import io.axoniq.build.sleep_on_time.issue_reporting.api.ReportContainerIssue
import io.axoniq.build.sleep_on_time.issue_reporting.api.ContainerIssueReported
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import java.time.LocalDateTime

/**
 * Event-sourced entity state for the Issue Reporting Service component.
 * Maintains the state of container issue reports including issue details,
 * guest information, and reporting metadata.
 */
@EventSourcedEntity
class IssueReportingState {
    private var issueId: String? = null
    private var guestId: String? = null
    private var description: String? = null
    private var reportedAt: LocalDateTime? = null
    private var severity: String? = null
    private var containerId: String? = null
    private var issueReported: Boolean = false

    fun getIssueId(): String? = issueId
    fun getGuestId(): String? = guestId
    fun getDescription(): String? = description
    fun getReportedAt(): LocalDateTime? = reportedAt
    fun getSeverity(): String? = severity
    fun getContainerId(): String? = containerId
    fun getIssueReported(): Boolean = issueReported

    @EntityCreator
    constructor()

    /**
     * Handles the ContainerIssueReported event to evolve the state.
     * Updates all issue-related properties when an issue is reported.
     */
    @EventSourcingHandler
    fun evolve(event: ContainerIssueReported) {
        this.issueId = event.issueId
        this.guestId = event.guestId
        this.description = event.description
        this.reportedAt = event.reportedAt
        this.severity = event.severity
        this.containerId = event.containerId
        this.issueReported = true
    }

    companion object {
        /**
         * Builds event criteria for loading issue reporting state.
         * Queries events tagged with the specific booking, guest, and container identifiers.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: ReportContainerIssue.TargetIdentifier): EventCriteria {
            return EventCriteria
                .havingTags(
                    Tag.of("Booking", id.bookingId),
                    Tag.of("Guest", id.guestId),
                    Tag.of("Container", id.containerId)
                )
                .andBeingOneOfTypes(
                    ContainerIssueReported::class.java.name
                )
        }
    }
}

