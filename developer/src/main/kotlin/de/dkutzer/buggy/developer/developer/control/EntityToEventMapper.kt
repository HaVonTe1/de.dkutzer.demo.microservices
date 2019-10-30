package de.dkutzer.buggy.developer.developer.control

import de.dkutzer.buggy.developer.developer.entity.Developer
import de.dkutzer.buggy.developer.developer.entity.DeveloperCreated
import de.dkutzer.buggy.developer.developer.entity.DeveloperDeleted
import de.dkutzer.buggy.developer.developer.entity.DeveloperUpdated


inline fun <reified T : MessageGateway.Event> Developer.toEvent(): MessageGateway.Event {
    return when (T::class) {
        DeveloperCreated::class -> DeveloperCreated(id,firstName,lastName)
        DeveloperUpdated::class -> DeveloperUpdated(id,firstName,lastName)
        DeveloperDeleted::class -> DeveloperDeleted(id)
        else -> throw UnknownEventException(this, T::class.simpleName)
    }
}