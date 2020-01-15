package de.dkutzer.buggy.planning.control

import de.dkutzer.buggy.planning.entity.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap

@Service
class DeveloperServices(val developerRepository: DeveloperRepository) {

    fun upsert(event: DeveloperEvent) = developerRepository.save(event.toEntity())

    fun delete(event: Event) = developerRepository.deleteById(event.id)

}

private fun DeveloperEvent.toEntity(): Developer = Developer(id, "$firstName $lastName")

@Service
class IssuesServices(val issuesRepository: IssuesRepository) {

    fun upsert(event: IssueEvent) = issuesRepository.save(event.toEntity())

    fun delete(event: Event) = issuesRepository.deleteById(event.id)

}

private fun IssueEvent.toEntity(): Issue = Issue(id, type, title, description, createdAt,assignee, points, status, priority)

@Service
class PlanningService(val issuesRepository: IssuesRepository, val developerRepository: DeveloperRepository) {
    private val capacityPerDev: Int = 5

    fun doPlanning():PlanningDto {

        val estimatedStories = issuesRepository.findAllByTypeAndStatusOrderByPriorityAsc(Type.STORY.name, Status.Estimated.name)
        val numberOfDevs = developerRepository.count().toInt()
        var capacity = numberOfDevs * capacityPerDev
        val issuesPerWeek = LinkedMultiValueMap<Int, IssueDto>()
        val remainingIssues = ArrayList<Issue>(estimatedStories)
        val issues = ArrayList<Issue>(remainingIssues)
        var currentWeek = 1;

        while (issues.isNotEmpty()){
            for (it: Issue in issues){
                val points = if(it.remainPoints>0)it.remainPoints else  it.points
                if(points <= capacity){
                    issuesPerWeek.add(currentWeek, it.toDto())
                    capacity-=points
                    remainingIssues.remove(it)
                } else if(capacity > 0 ){
                    val remainingPoints = points - capacity;
                    it.remainPoints = remainingPoints
                    issuesPerWeek.add(currentWeek, it.toDto())
                    break;
                }
            }
            issues.clear()
            issues.addAll(remainingIssues)
            ++currentWeek
            capacity = numberOfDevs * capacityPerDev
        }
        val weeks = ArrayList<Week>()
        issuesPerWeek.forEach { (weekNr, issueDto) ->
            weeks.add(Week(weekNr,issueDto))
        }

        val numberOfIssuesPerWeek = if(weeks.size!=0) (estimatedStories.size / weeks.size).toDouble() else 0.0
        return  PlanningDto(Summary(weeks.size,estimatedStories.size.toLong(),numberOfDevs.toLong(),numberOfIssuesPerWeek),weeks)

    }

}

private fun Issue.toDto(): IssueDto = IssueDto(type,title,description,createdAt,assignee,points,status,priority)