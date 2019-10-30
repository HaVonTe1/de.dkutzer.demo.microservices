package de.dkutzer.buggy.planning

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BuggyPlanningApplication

fun main(args: Array<String>) {
    runApplication<BuggyPlanningApplication>(*args)
}
