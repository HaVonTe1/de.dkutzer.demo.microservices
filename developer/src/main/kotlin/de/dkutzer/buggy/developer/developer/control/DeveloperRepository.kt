package de.dkutzer.buggy.developer.developer.control

import de.dkutzer.buggy.developer.developer.entity.Developer
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(collectionResourceRel = "developer", path = "developers")
interface DeveloperRepository : MongoRepository<Developer, String>{
}