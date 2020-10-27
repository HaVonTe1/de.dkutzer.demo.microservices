package de.dkutzer.buggy.developer.control

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import de.dkutzer.buggy.developer.entity.*
import io.smallrye.reactive.messaging.annotations.Broadcast
import org.eclipse.microprofile.reactive.messaging.*
import org.eclipse.microprofile.reactive.messaging.Message.of
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject


@ApplicationScoped
class MessageGateway {

    interface Event


    @Inject
    @field: Default
    lateinit var objectMapper: ObjectMapper

    @Inject
    @Channel("developer")
    @OnOverflow(OnOverflow.Strategy.BUFFER)
    var createdEventEmitter: Emitter<Event>? = null

    @Outgoing("developer")
    @Incoming("developer")
    @Broadcast
    @Throws(JsonProcessingException::class)
    fun process(developerEvent: Event): Message<String> {
        return of(objectMapper.writeValueAsString(developerEvent))
    }

    fun created(developer: Developer) {
        createdEventEmitter!!.send(developer.toEvent<DeveloperCreated>())
    }

    fun updated(developer: Developer) {
        createdEventEmitter!!.send(developer.toEvent<DeveloperUpdated>())
    }

    fun deleted(developer: Developer) {
        createdEventEmitter!!.send(developer.toEvent<DeveloperDeleted>())
    }
}