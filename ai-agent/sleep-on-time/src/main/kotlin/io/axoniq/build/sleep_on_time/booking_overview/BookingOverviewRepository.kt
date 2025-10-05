package io.axoniq.build.sleep_on_time.booking_overview

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repository interface for BookingOverviewEntity.
 * Provides data access operations for the Booking Overview View component.
 */
interface BookingOverviewRepository : JpaRepository<BookingOverviewEntity, String> {

    /**
     * Finds a booking overview by booking ID and guest ID.
     * Used by the GetBookingOverview query handler.
     */
    fun findByBookingIdAndGuestId(bookingId: String, guestId: String): BookingOverviewEntity?
}

