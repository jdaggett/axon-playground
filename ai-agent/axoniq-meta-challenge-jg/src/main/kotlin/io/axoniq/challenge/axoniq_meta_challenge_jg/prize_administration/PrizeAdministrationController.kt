package io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration

import io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/prize-administration")
class PrizeAdministrationController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PrizeAdministrationController::class.java)
    }

    @PostMapping("/select-winners")
    fun selectPrizeWinners(@RequestBody command: SelectPrizeWinners): ResponseEntity<String> {
        logger.info("Dispatching SelectPrizeWinners command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Prize winners selection accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch SelectPrizeWinners command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to select prize winners")
        }
    }

    @PostMapping("/announce-winners")
    fun announceSelectedPrizeWinners(@RequestBody command: AnnounceSelectedPrizeWinners): ResponseEntity<String> {
        logger.info("Dispatching AnnounceSelectedPrizeWinners command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Prize winners announcement accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch AnnounceSelectedPrizeWinners command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to announce prize winners")
        }
    }

    @PostMapping("/claim-prize")
    fun claimPrize(@RequestBody command: ClaimPrize): ResponseEntity<String> {
        logger.info("Dispatching ClaimPrize command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Prize claim accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ClaimPrize command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to claim prize")
        }
    }
}

