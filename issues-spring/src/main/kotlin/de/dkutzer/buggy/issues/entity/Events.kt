package de.dkutzer.buggy.issues.entity

import de.dkutzer.buggy.issues.control.MessageGateway
import java.time.LocalDate

data class IssueCreated(
        val id: String,
        val type: Type,
        val title: String,
        val description: String,
        val createdAt: LocalDate,
        val assignee: String,
        val points: Int,
        val status: Status,
        val priority: Priority
) : MessageGateway.Event

data class IssueUpdated(
        val id: String,
        val type: Type,
        val title: String,
        val description: String,
        val createdAt: LocalDate,
        val assignee: String,
        val points: Int,
        val status: Status,
        val priority: Priority
) : MessageGateway.Event


data class IssueDeleted(
        val id: String
) : MessageGateway.Event