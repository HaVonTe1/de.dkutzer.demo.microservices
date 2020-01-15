package de.dkutzer.buggy.developer

import de.dkutzer.buggy.developer.control.DeveloperRepository
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.ToDoubleFunction

@Configuration
class AppConfig (val meterRegistry: MeterRegistry,val developerRepository: DeveloperRepository){

    @Autowired
    fun setCounter(meterRegistry: MeterRegistry) {
        meterRegistry.gauge("de.dkutzer.buggy.developers.size", developerRepository, ToDoubleFunction { it -> it.count().toFloat().toDouble() } )!!

    }
}