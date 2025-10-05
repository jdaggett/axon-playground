package io.axoniq.build.sleep_on_time.booking_overview

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * JPA Entity representing the booking overview read model.
 * This entity stores booking information for the Booking Overview View component.
 */
@Entity
@Table(name = "booking_overview")
data class BookingOverviewEntity(
    @Id
    @Column(name = "booking_id")
    val bookingId: String,

    @Column(name = "guest_id", nullable = false)
    val guestId: String,
    
    @Column(name = "container_id", nullable = false)
    val containerId: String,
    
    @Column(name = "container_location", nullable = false)
    val containerLocation: String,

    @Column(name = "check_in_time", nullable = true)
    val checkInTime: LocalDateTime?,

    @Column(name = "check_out_time", nullable = true)
    val checkOutTime: LocalDateTime?,

    @Column(name = "has_reported_issues", nullable = false)
    val hasReportedIssues: Boolean,
    
    @Column(name = "can_open_door", nullable = false)
    val canOpenDoor: Boolean,

    @Column(name = "status", nullable = false)
    val status: String
) {
    constructor() : this("", "", "", "", null, null, false, false, "")
}

