package de.dkutzer.buggy.planning.boundary

import de.dkutzer.buggy.planning.control.DeveloperServices
import de.dkutzer.buggy.planning.control.IssuesServices
import de.dkutzer.buggy.planning.entity.*
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Service

@Service
@EnableBinding(DevelopersChannel::class)
class DevelopersMessageListener(val developerService: DeveloperServices, val issuesService: IssuesServices) {

    @StreamListener(target = DevelopersChannel.INPUT, condition = "headers['event']=='DeveloperCreated'")
    fun handleDeveloperCreatedEvent(developerCreated: DeveloperCreated) {developerService.upsert(developerCreated)}

    @StreamListener(target = DevelopersChannel.INPUT, condition = "headers['event']=='DeveloperUpdated'")
    fun handleDeveloperUpdatedEvent(developerUpdated: DeveloperUpdated) {developerService.upsert(developerUpdated)}

    @StreamListener(target = DevelopersChannel.INPUT, condition = "headers['event']=='DeveloperDeleted'")
    fun handleDeveloperDeletedEvent(developerDeleted: DeveloperDeleted) {developerService.delete(developerDeleted)}

}

@Service
@EnableBinding(IssuesChannel::class)
class IssuesMessageListener(val issuesService: IssuesServices) {

    @StreamListener(target = IssuesChannel.INPUT, condition = "headers['event']=='IssueCreated'")
    fun handleIssueCreatedEvent(issueCreated: IssueCreated) {issuesService.upsert(issueCreated)}

    @StreamListener(target = IssuesChannel.INPUT, condition = "headers['event']=='IssueUpdated'")
    fun handleIssueUpdatedEvent(issueUpdated: IssueUpdated) {issuesService.upsert(issueUpdated)}

    @StreamListener(target = IssuesChannel.INPUT, condition = "headers['event']=='IssueDeleted'")
    fun handleIssueDeletedEvent(issueDeleted: IssueDeleted) {issuesService.delete(issueDeleted)}
    
}