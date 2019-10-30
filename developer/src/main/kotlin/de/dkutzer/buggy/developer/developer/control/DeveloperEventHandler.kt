package de.dkutzer.buggy.developer.developer.control

import de.dkutzer.buggy.developer.developer.entity.Developer
import de.dkutzer.buggy.developer.developer.entity.DeveloperCreated
import de.dkutzer.buggy.developer.developer.entity.DeveloperDeleted
import de.dkutzer.buggy.developer.developer.entity.DeveloperUpdated
import org.springframework.data.rest.core.annotation.HandleAfterCreate
import org.springframework.data.rest.core.annotation.HandleAfterDelete
import org.springframework.data.rest.core.annotation.HandleAfterSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

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