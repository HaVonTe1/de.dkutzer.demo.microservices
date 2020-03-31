package de.dkutzer.buggy.developer

import de.dkutzer.buggy.developer.control.DeveloperRepository
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter
import java.util.function.ToDoubleFunction

@Configuration
class AppConfig(val meterRegistry: MeterRegistry, val developerRepository: DeveloperRepository) {

    @Autowired
    fun setCounter(meterRegistry: MeterRegistry) {
        meterRegistry.gauge("de.dkutzer.buggy.developers.size", developerRepository, ToDoubleFunction { it -> it.count().toFloat().toDouble() })!!

    }
    

    

}