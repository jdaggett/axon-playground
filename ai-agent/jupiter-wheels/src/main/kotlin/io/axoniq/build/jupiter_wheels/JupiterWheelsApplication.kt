package io.axoniq.build.jupiter_wheels;

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JupiterWheelsApplication

fun main(args: Array<String>) {
	runApplication<JupiterWheelsApplication>(*args)
}