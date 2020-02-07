package de.dkutzer.buggy.developer

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc


@SpringBootApplication
@EnableSwagger2WebMvc
@Import(SpringDataRestConfiguration::class)
class DeveloperApplication

fun main(args: Array<String>) {
    runApplication<DeveloperApplication>(*args)


}

@Bean
fun timedAspect(registry: MeterRegistry?): TimedAspect? {
    return TimedAspect(registry!!)
}
