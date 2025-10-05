package io.axoniq.build.apex_racing_labs.driver_rating

import io.axoniq.build.apex_racing_labs.driver_rating.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Driver Performance Rating Service component.
 * Provides HTTP endpoints for rating driver performance and updating ratings.
 */
@RestController
@RequestMapping("/api/driver-ratings")
class DriverPerformanceRatingServiceController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverPerformanceRatingServiceController::class.java)
    }

    /**
     * Endpoint to rate a driver's performance in a race.
     * Accepts a RateDriverPerformance command and dispatches it through the command gateway.
     *
     * @param request The RateDriverPerformance request containing rating information
     * @return ResponseEntity with HTTP 202 Accepted on success or appropriate error response
     */
    @PostMapping("/rate")
    fun rateDriverPerformance(@RequestBody request: RateDriverPerformance): ResponseEntity<String> {
        logger.info("Received request to rate driver performance: $request")

        return try {
            commandGateway.sendAndWait(request)
            logger.info("Successfully dispatched RateDriverPerformance command")
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Driver performance rating accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RateDriverPerformance command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to rate driver performance")
        }
    }

    /**
     * Endpoint to update an existing driver rating.
     * Accepts an UpdateDriverRating command and dispatches it through the command gateway.
     *
     * @param request The UpdateDriverRating request containing updated rating information
     * @return ResponseEntity with HTTP 202 Accepted on success or appropriate error response
     */
    @PostMapping("/update")
    fun updateDriverRating(@RequestBody request: UpdateDriverRating): ResponseEntity<String> {
        logger.info("Received request to update driver rating: $request")

        return try {
            commandGateway.sendAndWait(request)
            logger.info("Successfully dispatched UpdateDriverRating command")
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Driver rating update accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch UpdateDriverRating command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update driver rating")
        }
    }
}

