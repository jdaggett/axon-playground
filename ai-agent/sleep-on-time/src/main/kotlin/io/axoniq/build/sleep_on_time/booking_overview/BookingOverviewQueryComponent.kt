package io.axoniq.build.sleep_on_time.booking_overview

import io.axoniq.build.sleep_on_time.booking_overview.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Query component for the Booking Overview View.
 * Handles queries and events related to booking overview information for guests.
 */
@Component
class BookingOverviewQueryComponent(
    private val repository: BookingOverviewRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BookingOverviewQueryComponent::class.java)
    }

    /**
     * Handles GetBookingOverview queries.
     * Returns booking overview details for the specified booking and guest.
     */
    @QueryHandler
    fun handle(query: GetBookingOverview): BookingOverviewDetails? {
        logger.info("Processing GetBookingOverview query for bookingId: ${query.bookingId}, guestId: ${query.guestId}")

        val entity = repository.findByBookingIdAndGuestId(query.bookingId, query.guestId)

        return entity?.let {
            BookingOverviewDetails(
                containerLocation = it.containerLocation,
                checkInTime = it.checkInTime,
                checkOutTime = it.checkOutTime,
                bookingId = it.bookingId,
                hasReportedIssues = it.hasReportedIssues,
                containerId = it.containerId,
                canOpenDoor = it.canOpenDoor,
                status = it.status
            )
        }
    }

    /**
     * Handles ContainerObtained events.
     * Creates or updates booking overview when a container is obtained.
     */
    @EventHandler
    fun on(event: ContainerObtained) {
        logger.info("Processing ContainerObtained event for bookingId: ${event.bookingId}")

        val existingEntity = repository.findByBookingIdAndGuestId(event.bookingId, event.guestId)

        val entity = existingEntity?.copy(
            containerId = event.containerId,
            status = "CONTAINER_OBTAINED",
            canOpenDoor = true
        ) ?: BookingOverviewEntity(
            bookingId = event.bookingId,
            guestId = event.guestId,
            containerId = event.containerId,
            containerLocation = "", // Will be updated when more information is available
            checkInTime = null,
            checkOutTime = null,
            hasReportedIssues = false,
            canOpenDoor = true,
            status = "CONTAINER_OBTAINED"
        )

        repository.save(entity)
        logger.debug("Saved booking overview entity for bookingId: ${event.bookingId}")
    }

    /**
     * Handles GuestCheckedIn events.
     * Updates booking overview when a guest checks in.
     */
    @EventHandler
    fun on(event: GuestCheckedIn) {
        logger.info("Processing GuestCheckedIn event for bookingId: ${event.bookingId}")

        val existingEntity = repository.findByBookingIdAndGuestId(event.bookingId, event.guestId)

        val entity = existingEntity?.copy(
            checkInTime = event.checkedInAt,
            containerId = event.containerId,
            status = "CHECKED_IN"
        ) ?: BookingOverviewEntity(
            bookingId = event.bookingId,
            guestId = event.guestId,
            containerId = event.containerId,
            containerLocation = "", // Will be updated when more information is available
            checkInTime = event.checkedInAt,
            checkOutTime = null,
            hasReportedIssues = false,
            canOpenDoor = true,
            status = "CHECKED_IN"
        )
        
        repository.save(entity)
        logger.debug("Updated booking overview with check-in time for bookingId: ${event.bookingId}")
    }

    /**
     * Handles GuestCheckedOut events.
     * Updates booking overview when a guest checks out.
     */
    @EventHandler
    fun on(event: GuestCheckedOut) {
        logger.info("Processing GuestCheckedOut event for bookingId: ${event.bookingId}")

        val existingEntity = repository.findByBookingIdAndGuestId(event.bookingId, event.guestId)

        existingEntity?.let { entity ->
            val updatedEntity = entity.copy(
                checkOutTime = event.timestamp,
                status = "CHECKED_OUT",
                canOpenDoor = false
            )

            repository.save(updatedEntity)
            logger.debug("Updated booking overview with check-out time for bookingId: ${event.bookingId}")
        } ?: logger.warn("No existing booking overview found for bookingId: ${event.bookingId}, guestId: ${event.guestId}")
    }

    /**
     * Handles ContainerIssueReported events.
     * Updates booking overview when an issue is reported.
     */
    @EventHandler
    fun on(event: ContainerIssueReported) {
        logger.info("Processing ContainerIssueReported event for bookingId: ${event.bookingId}")

        val existingEntity = repository.findByBookingIdAndGuestId(event.bookingId, event.guestId)

        existingEntity?.let { entity ->
            val updatedEntity = entity.copy(
                hasReportedIssues = true,
                containerId = event.containerId
            )

            repository.save(updatedEntity)
            logger.debug("Updated booking overview with reported issue for bookingId: ${event.bookingId}")
        } ?: logger.warn("No existing booking overview found for bookingId: ${event.bookingId}, guestId: ${event.guestId}")
    }
}

