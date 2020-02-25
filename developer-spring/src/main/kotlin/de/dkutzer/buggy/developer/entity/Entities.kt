package de.dkutzer.buggy.developer.entity

import de.dkutzer.buggy.developer.control.MessageGateway
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Document(collection = "developers")
@TypeAlias(value = "developer")
data class Developer(
        @Id
        val id: String,
        @NotNull
        @Size(min = 2, max = 50)
        val firstName: String,
        @NotNull
        @Size(min = 2, max = 50)
        val lastName: String
)

inline fun <reified T : MessageGateway.Event> Developer.toEvent(): MessageGateway.Event {
    return when (T::class) {
        DeveloperCreated::class -> DeveloperCreated(id, firstName, lastName)
        DeveloperUpdated::class -> DeveloperUpdated(id, firstName, lastName)
        DeveloperDeleted::class -> DeveloperDeleted(id)
        else -> throw IllegalArgumentException(T::class.simpleName)
    }
}