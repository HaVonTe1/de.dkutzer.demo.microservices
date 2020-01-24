package de.dkutzer.buggy.issues.control

import de.dkutzer.buggy.issues.entity.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.*
import org.springframework.stereotype.Component
import org.javers.spring.annotation.JaversSpringDataAuditable

@RepositoryRestResource(collectionResourceRel = "issues", path = "stories")
@JaversSpringDataAuditable
interface StoryRestRepository : MongoRepository<Story, String> {
}

@RepositoryRestResource(collectionResourceRel = "issues", path = "bugs")
@JaversSpringDataAuditable
interface BugRestRepository : MongoRepository<Bug, String> {
}

@RepositoryEventHandler
@Component
class IssueEventHandler(val messageGateway: MessageGateway) {

    @HandleAfterCreate
    fun handleStoryCreated(ent: Story) = messageGateway.send(ent.toEvent<IssueCreated>())

    @HandleAfterSave
    fun handleStoryUpdated(ent: Story) = messageGateway.send(ent.toEvent<IssueUpdated>())

    @HandleAfterDelete
    fun handleStoryDeleted(ent: Story) = messageGateway.send(ent.toEvent<IssueDeleted>())

    @HandleAfterCreate
    fun handleBugCreated(ent: Bug) = messageGateway.send(ent.toEvent<IssueCreated>())

    @HandleAfterSave
    fun handleBugUpdated(ent: Bug) = messageGateway.send(ent.toEvent<IssueUpdated>())

    @HandleAfterDelete
    fun handleBugDeleted(ent: Bug) = messageGateway.send(ent.toEvent<IssueDeleted>())


}