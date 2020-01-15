package de.dkutzer.buggy.issues.entity;

import de.dkutzer.buggy.issues.control.MessageGateway
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@TypeAlias("Story")
@Document(collection = "issues")
@CompoundIndexes( value = [
    CompoundIndex(name = "status_type", def = "{'status' : 1, 'type': 1}")
])
data class Story (
        @Id
        val id: String,
        @NotNull
        var type: Type = Type.STORY,
        @NotNull
        val title: String,
        val description: String,
        @Indexed
        val assignee: String,
        @Min(0)
        val points: Int,
        val status: Status,
        val priority: Priority

)
@TypeAlias("Bug")
@Document(collection = "issues")
data class Bug (
        @Id
        val id: String,
        @NotNull
        var type: Type = Type.BUG,
        @NotNull
        val title: String,
        val description: String,
        @Indexed
        val assignee: String,
        val status: Status,
        val priority: Priority
)

enum class Priority {
        Critical, Major, Minor
}

enum class Status {
        New, Verified, Resolved, Estimated, Completed
}

enum class Type {
        STORY,BUG
}

inline fun <reified T : MessageGateway.Event> Story.toEvent(): MessageGateway.Event {
        return when (T::class) {
                IssueCreated::class -> IssueCreated(id,Type.STORY,title,description, LocalDate.now(),assignee,points,status,priority)
                IssueUpdated::class -> IssueUpdated(id,Type.STORY,title,description, LocalDate.now(),assignee,points,status,priority)
                IssueDeleted::class -> IssueDeleted(id)
                else -> throw IllegalArgumentException(T::class.simpleName)
        }
}
inline fun <reified T : MessageGateway.Event> Bug.toEvent(): MessageGateway.Event {
        return when (T::class) {
                IssueCreated::class -> IssueCreated(id,Type.BUG,title,description, LocalDate.now(),assignee,0,status,priority)
                IssueUpdated::class -> IssueUpdated(id,Type.BUG,title,description, LocalDate.now(),assignee,0,status,priority)
                IssueDeleted::class -> IssueDeleted(id)
                else -> throw IllegalArgumentException(T::class.simpleName)
        }
}