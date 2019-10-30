package de.dkutzer.buggy.planning.entity

import java.time.LocalDate


interface Event {
    val id: String
}

interface DeveloperEvent : Event {
    val firstName: String
    val lastName: String
}

data class DeveloperCreated(override val id: String, override val firstName: String, override val lastName: String) : DeveloperEvent {}

data class DeveloperUpdated(override val id: String, override val firstName: String, override val lastName: String) : DeveloperEvent {}

data class DeveloperDeleted(override val id: String) : Event {}

interface IssueEvent : Event {
    val type: Type
    val title: String
    val description: String
    val createdAt: LocalDate
    val assignee: String
    val points: Int
    val status: Status
    val priority: Priority

}

data class IssueCreated(override val id: String, override val type: Type, override val title: String, override val description: String, override val createdAt: LocalDate, override val assignee: String, override val points: Int, override val status: Status, override val priority: Priority) : IssueEvent {}
data class IssueUpdated(override val id: String, override val type: Type, override val title: String, override val description: String, override val createdAt: LocalDate, override val assignee: String, override val points: Int, override val status: Status, override val priority: Priority) : IssueEvent {}
data class IssueDeleted(override val id: String) : Event {}


enum class Priority {
    Critical, Major, Minor
}

enum class Status {
    New, Verified, Resolved, Estimated, Completed
}

enum class Type {
    STORY, BUG
}

