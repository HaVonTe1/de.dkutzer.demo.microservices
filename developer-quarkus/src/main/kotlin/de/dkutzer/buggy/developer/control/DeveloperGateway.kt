package de.dkutzer.buggy.developer.control

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import de.dkutzer.buggy.developer.entity.*
import io.smallrye.reactive.messaging.amqp.AmqpMessage
import io.smallrye.reactive.messaging.annotations.Broadcast
import io.smallrye.reactive.messaging.annotations.Channel
import io.smallrye.reactive.messaging.annotations.Emitter
import io.smallrye.reactive.messaging.annotations.OnOverflow
import io.vertx.amqp.impl.AmqpMessageImpl
import org.apache.qpid.proton.amqp.messaging.AmqpValue
import org.apache.qpid.proton.message.Message
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class DeveloperGateway {

    private val objectMapper = ObjectMapper()

    @Inject
    @Channel("developer")
    @OnOverflow(OnOverflow.Strategy.BUFFER)
    var createdEventEmitter: Emitter<DeveloperEvent>? = null

    @Outgoing("developer")
    @Incoming("developer")
    @Broadcast
    @Throws(JsonProcessingException::class)
    fun process(developerEvent: DeveloperEvent): org.eclipse.microprofile.reactive.messaging.Message<String> {
        val message1 = Message.Factory.create()
        message1.body = AmqpValue(objectMapper.writeValueAsString(developerEvent))
        message1.contentType="application/json"
        message1.subject = developerEvent.javaClass.simpleName
        message1.contentEncoding = Charsets.UTF_8.name()
        val amqpMessageImpl = AmqpMessageImpl(message1)
        return AmqpMessage<String>(amqpMessageImpl)
    }

    fun created(developer: Developer) {
        createdEventEmitter!!.send(DeveloperCreatedEvent(developer))
    }
    fun updated(developer: Developer) {
        createdEventEmitter!!.send(DeveloperUpdatedEvent(developer))
    }
    fun deleted(id:String) {
        createdEventEmitter!!.send(DeveloperDeletedEvent(id))
    }
}