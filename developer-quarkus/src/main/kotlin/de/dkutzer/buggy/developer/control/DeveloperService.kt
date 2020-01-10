package de.dkutzer.buggy.developer.control

import de.dkutzer.buggy.developer.entity.Developer
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.NotFoundException

@ApplicationScoped
class DeveloperService(@field:Inject var developerRepository: DeveloperRepository) {

    @Inject
    @field: Default
    lateinit var messageGateway: MessageGateway

    fun findAll(): Iterable<Developer> {
        return developerRepository.findAll()
    }

    fun findById(id: String): Developer {
        return developerRepository.findById(id).orElseThrow { NotFoundException() }
    }

    fun deleteById(id: String): Boolean {
        if (developerRepository.exists(id)) {
            developerRepository.delete(id)
            messageGateway.deleted(Developer(id,"",""))
            return true
        }
        return false
    }

    fun upsert(developer: Developer): Developer {
        val existed = developerRepository.exists(developer.id)


        val result = (developerRepository.upsert(developer))

        if (existed) {
            messageGateway.updated(result)
        } else {

            messageGateway.created(result)
        }
        return result
    }

    fun exists(id: String?): Boolean {
        return id != null && id.isNotEmpty() && developerRepository.exists(id)
    }

    fun deleteAll() {

        val all = this.developerRepository.findAll()
        this.developerRepository.deleteAll()
        all.forEach { messageGateway.deleted(it) }
    }

}