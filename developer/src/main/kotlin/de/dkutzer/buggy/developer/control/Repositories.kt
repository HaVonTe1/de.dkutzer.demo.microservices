package de.dkutzer.buggy.developer.control

import de.dkutzer.buggy.developer.entity.*
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.*
import org.springframework.stereotype.Component

@RepositoryRestResource(collectionResourceRel = "developer", path = "developers")
@JaversSpringDataAuditable
interface DeveloperRepository : MongoRepository<Developer, String>{
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


}