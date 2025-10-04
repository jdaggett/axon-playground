package io.axoniq.build.jupiter_wheels.bike_return_management

import io.axoniq.build.jupiter_wheels.bike_return_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced entity state for the Bike Return Management component.
 * Maintains state for bike return process and validation.
 */
@EventSourcedEntity
class BikeReturnState {

    private var returnLocation: String? = null
    private var photoSubmitted: Boolean = false
    private var surveySubmitted: Boolean = false
    private var inspectionCompleted: Boolean = false
    private var rentalId: String? = null
    private var bikeId: String? = null

    fun getReturnLocation(): String? = returnLocation
    fun getPhotoSubmitted(): Boolean = photoSubmitted
    fun getSurveySubmitted(): Boolean = surveySubmitted
    fun getInspectionCompleted(): Boolean = inspectionCompleted
    fun getRentalId(): String? = rentalId
    fun getBikeId(): String? = bikeId

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for BikePhotoSubmitted event.
     * Updates state when a bike photo is submitted.
     */
    @EventSourcingHandler
    fun evolve(event: BikePhotoSubmitted) {
        this.photoSubmitted = true
        this.rentalId = event.rentalId
    }

    /**
     * Event sourcing handler for BikeReturned event.
     * Updates state when a bike is returned at a location.
     */
    @EventSourcingHandler
    fun evolve(event: BikeReturned) {
        this.returnLocation = event.returnLocation
        this.rentalId = event.rentalId
        this.bikeId = event.bikeId
    }

    /**
     * Event sourcing handler for ReturnSurveySubmitted event.
     * Updates state when a return survey is submitted.
     */
    @EventSourcingHandler
    fun evolve(event: ReturnSurveySubmitted) {
        this.surveySubmitted = true
        this.rentalId = event.rentalId
    }

    /**
     * Event sourcing handler for PhotoFlaggedForReview event.
     * Updates state when a photo is flagged for review.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: PhotoFlaggedForReview) {
        this.rentalId = event.rentalId
    }

    /**
     * Event sourcing handler for BikeInspectionCompleted event.
     * Updates state when bike inspection is completed.
     */
    @EventSourcingHandler
    fun evolve(event: BikeInspectionCompleted) {
        this.inspectionCompleted = true
        this.rentalId = event.rentalId
        this.bikeId = event.bikeId
    }

    companion object {
        /**
         * Event criteria builder for loading events related to bike return management.
         * Filters events by rental ID tag.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(rentalId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Rental", rentalId))
                .andBeingOneOfTypes(
                    BikePhotoSubmitted::class.java.name,
                    BikeReturned::class.java.name,
                    ReturnSurveySubmitted::class.java.name,
                    PhotoFlaggedForReview::class.java.name,
                    BikeInspectionCompleted::class.java.name
                )
        }
    }
}

