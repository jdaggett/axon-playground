package io.axoniq.build.sleep_on_time;

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SleepOnTimeApplication

fun main(args: Array<String>) {
	runApplication<SleepOnTimeApplication>(*args)
}