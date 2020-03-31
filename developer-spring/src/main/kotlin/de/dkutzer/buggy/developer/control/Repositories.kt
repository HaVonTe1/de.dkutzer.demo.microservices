package de.dkutzer.buggy.developer.control

import de.dkutzer.buggy.developer.entity.*
import io.micrometer.core.instrument.Gauge
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.slf4j.MDC
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.*
import org.springframework.stereotype.Component
import java.util.*

// tag::HATEOS[]
@RepositoryRestResource(collectionResourceRel = "developer", path = "developers")
@JaversSpringDataAuditable
interface DeveloperRepository : MongoRepository<Developer, String>{
// end::HATEOS[]


}

@RepositoryEventHandler
@Component
class DeveloperEventHandler(val messageGateway: MessageGateway) {

    @HandleAfterCreate
    fun handleDeveloperCreated(dev: Developer) = messageGateway.send(dev.toEvent<DeveloperCreated>())

    @HandleAfterSave
    fun handleDeveloperUpdated(dev: Developer) = messageGateway.send(dev.toEvent<DeveloperUpdated>())

    @HandleAfterDelete
    fun handleDeveloperDeleted(dev: Developer) = messageGateway.send(dev.toEvent<DeveloperDeleted>())


    @HandleBeforeCreate
    fun handleDeveloperBeforeCreate(dev:Developer) = MDC.put("CorrId", UUID.randomUUID().toString())

}