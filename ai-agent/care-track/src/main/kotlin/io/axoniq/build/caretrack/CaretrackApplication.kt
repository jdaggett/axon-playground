package io.axoniq.build.caretrack;

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CaretrackApplication

fun main(args: Array<String>) {
	runApplication<CaretrackApplication>(*args)
}