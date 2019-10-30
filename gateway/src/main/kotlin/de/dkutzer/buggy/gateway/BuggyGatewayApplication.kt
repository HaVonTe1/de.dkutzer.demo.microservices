package de.dkutzer.buggy.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BuggyGatewayApplication

fun main(args: Array<String>) {
    runApplication<BuggyGatewayApplication>(*args)
}
