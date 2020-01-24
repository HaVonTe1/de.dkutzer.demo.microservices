package de.dkutzer.buggy.developer

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication

class DeveloperApplication

fun main(args: Array<String>) {
    runApplication<DeveloperApplication>(*args)


}

@Bean
fun timedAspect(registry: MeterRegistry?): TimedAspect? {
    return TimedAspect(registry!!)
}
