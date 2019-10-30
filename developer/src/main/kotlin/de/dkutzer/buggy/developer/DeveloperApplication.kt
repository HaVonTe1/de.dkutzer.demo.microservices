package de.dkutzer.buggy.developer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DeveloperApplication

fun main(args: Array<String>) {
    runApplication<DeveloperApplication>(*args)

//    @Bean
//    fun developerEventHandler(messageGateway: MessageGateway): DeveloperEventHandler {
//        return DeveloperEventHandler(messageGateway )
//    }
}
