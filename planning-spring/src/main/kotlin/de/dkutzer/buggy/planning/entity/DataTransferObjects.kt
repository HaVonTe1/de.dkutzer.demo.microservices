package de.dkutzer.buggy.planning.entity

import java.time.LocalDate

data class PlanningDto(
        val summary: Summary,
        val weeks: List<Week>
)

data class Week(
        val number: Int,
        val issues: List<IssueDto>
)

data class Summary(
        val weeks: Int,
        val issues: Long,
        val developers: Long,
        val issuesPerWeek: Double
)

data class IssueDto(
        val type: Type,
        val title: String,
        val description: String,
        val createdAt: LocalDate,
        val assignee: String,
        val points: Int,
        val status: Status,
        val priority: Priority
)

