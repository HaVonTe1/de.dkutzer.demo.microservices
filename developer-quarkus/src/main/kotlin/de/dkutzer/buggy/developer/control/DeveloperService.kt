package de.dkutzer.buggy.developer.control

import com.fasterxml.jackson.core.JsonProcessingException
import de.dkutzer.buggy.developer.entity.Developer
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.NotFoundException

@ApplicationScoped
class DeveloperService(@field:Inject var developerRepository: DeveloperRepository) {

    @Inject
    @field: Default
    lateinit var developerGateway: DeveloperGateway

    fun findAll(): Iterable<Developer> {
        return developerRepository.findAll()
    }

    fun findById(id: String): Developer {
        return developerRepository.findById(id).orElseThrow { NotFoundException() }
    }

    fun deleteById(id: String): Boolean {
        if (developerRepository.exists(id)) {
            developerRepository.delete(id)
            developerGateway.deleted(id)
            return true
        }
        return false
    }

    fun upsert(developer: Developer): Developer {
        if (developerRepository.upsert(developer)) {

            developerGateway.updated(developer)
        } else {

            developerGateway.created(developer)
        }
        return developer
    }

    fun exists(id: String?): Boolean {
        return id != null && id.isNotEmpty() && developerRepository.exists(id)
    }

}