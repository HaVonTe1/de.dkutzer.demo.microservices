package de.dkutzer.buggy.planning.control

import de.dkutzer.buggy.planning.entity.Developer
import de.dkutzer.buggy.planning.entity.Issue
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface IssuesRepository : MongoRepository<Issue, String> {
    fun findAllByTypeAndStatusOrderByPriorityAsc(type: String, status: String): List<Issue>

}

@Repository
interface DeveloperRepository : MongoRepository<Developer, String> {


}