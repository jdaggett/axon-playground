package io.axoniq.build.apex_racing_labs.driver_management

import io.axoniq.build.apex_racing_labs.driver_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced state entity for Driver Management Service.
 * Maintains the current state of a driver based on events.
 */
@EventSourcedEntity
class DriverManagementServiceState {
    private var teamId: String? = null
    private var driverId: String? = null
    private var active: Boolean = false
    private var driverName: String? = null

    fun getTeamId(): String? = teamId
    fun getDriverId(): String? = driverId
    fun isActive(): Boolean = active
    fun getDriverName(): String? = driverName

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for DriverCreated event.
     * Updates the state when a driver is created.
     *
     * @param event The DriverCreated event
     */
    @EventSourcingHandler
    fun evolve(event: DriverCreated) {
        this.teamId = event.teamId
        this.driverId = event.driverId
        this.driverName = event.driverName
        this.active = true
    }

    /**
     * Event sourcing handler for DriverRemoved event.
     * Updates the state when a driver is removed.
     * 
     * @param event The DriverRemoved event
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: DriverRemoved) {
        this.active = false
    }

    companion object {
        /**
         * Event criteria builder for loading driver events.
         * 
         * @param id The driver identifier
         * @return EventCriteria for loading relevant driver events
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Driver", id))
                .andBeingOneOfTypes(
                    DriverCreated::class.java.name,
                    DriverRemoved::class.java.name
                )
        }
    }
}

