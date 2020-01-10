package de.dkutzer.buggy.developer.entity

import de.dkutzer.buggy.developer.control.MessageGateway
import org.bson.Document
import java.util.*

data class Developer (

        val id: String?,
        val firstName: String,
        val lastName: String
)
inline fun <reified T : MessageGateway.Event> Developer.toEvent(): MessageGateway.Event {
    return when (T::class) {
        DeveloperCreated::class -> DeveloperCreated(id,firstName,lastName)
        DeveloperUpdated::class -> DeveloperUpdated(id,firstName,lastName)
        DeveloperDeleted::class -> DeveloperDeleted(id!!)
        else -> throw IllegalArgumentException(T::class.simpleName)
    }
}

public inline fun  Document.toEntity(): Developer =  Developer(this.get("id",UUID.randomUUID().toString()),this.getString("firstName"),this.getString("lastName"))