package de.dkutzer.buggy.planning.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@TypeAlias("Issue")
@Document(collection = "issues")
data class Issue(
        @Id
        val id: String,
        @NotNull
        val type: Type,
        @NotNull
        val title: String,
        val description: String,
        val createdAt: LocalDate,
        @Indexed
        val assignee: String,
        @Min(0)
        val points: Int,
        val status: Status,
        val priority: Priority

) {
    var remainPoints: Int = 0
}

@TypeAlias("Developer")
@Document(collection = "developer")
data class Developer(
        @Id
        val id: String,
        val name: String

)