package io.axoniq.build.dance_test.lesson_package_management

import io.axoniq.build.dance_test.lesson_package_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * Event sourced entity that maintains the state of a lesson package for the Lesson Package Management component.
 * This entity tracks lesson package details including count, duration, activity status, and associated identifiers.
 */
@EventSourcedEntity
class LessonPackageManagementState {

    private var lessonCount: Int = 0
    private var packageId: String? = null
    private var lessonDuration: Int = 0
    private var isActive: Boolean = false
    private var studentId: String? = null
    private var instructorId: String? = null
    private var price: Double = 0.0

    // Getter methods for accessing the state in command handlers
    fun getLessonCount(): Int = lessonCount
    fun getPackageId(): String? = packageId
    fun getLessonDuration(): Int = lessonDuration
    fun getIsActive(): Boolean = isActive
    fun getStudentId(): String? = studentId
    fun getInstructorId(): String? = instructorId
    fun getPrice(): Double = price

    @EntityCreator
    constructor()
    /**
     * Handles CustomLessonPackageCreated event to initialize the lesson package state.
     * Sets all package properties and marks it as active.
     */
    @EventSourcingHandler
    fun evolve(event: CustomLessonPackageCreated) {
        this.packageId = event.packageId
        this.lessonCount = event.lessonCount
        this.lessonDuration = event.lessonDuration
        this.studentId = event.studentId
        this.instructorId = event.instructorId
        this.price = event.price
        this.isActive = true
    }

    /**
     * Handles LessonPackageDeleted event to deactivate the lesson package.
     * Marks the package as inactive when deleted.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: LessonPackageDeleted) {
        this.isActive = false
    }

    companion object {
        /**
         * Builds event criteria for loading lesson package events.
         * Filters events by packageId tag and includes relevant event types.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(packageId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("LessonPackage", packageId))
                .andBeingOneOfTypes(
                    CustomLessonPackageCreated::class.java.name,
                    LessonPackageDeleted::class.java.name
                )
        }
    }
}