package io.axoniq.build.sleep_on_time.booking_overview

import io.axoniq.build.sleep_on_time.booking_overview.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the Booking Overview View component.
 * Exposes endpoints to query booking overview information for guests.
 */
@RestController
@RequestMapping("/api/booking-overview")
class BookingOverviewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BookingOverviewController::class.java)
    }

    /**
     * Gets booking overview details for a specific booking and guest.
     *
     * @param bookingId The ID of the booking
     * @param guestId The ID of the guest
     * @return BookingOverviewDetails containing the booking information
     */
    @GetMapping("/{bookingId}/guest/{guestId}")
    fun getBookingOverview(
        @PathVariable bookingId: String,
        @PathVariable guestId: String
    ): CompletableFuture<BookingOverviewDetails> {
        logger.info("REST request for booking overview - bookingId: $bookingId, guestId: $guestId")
        
        val query = GetBookingOverview(bookingId = bookingId, guestId = guestId)
        return queryGateway.query(query, BookingOverviewDetails::class.java, null)
    }
}