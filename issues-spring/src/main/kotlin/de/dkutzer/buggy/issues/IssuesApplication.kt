package de.dkutzer.buggy.issues

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IssuesApplication

fun main(args: Array<String>) {
    runApplication<IssuesApplication>(*args)
}
