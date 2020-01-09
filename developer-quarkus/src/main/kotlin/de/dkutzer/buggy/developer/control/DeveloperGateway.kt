package de.dkutzer.buggy.developer.control

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import de.dkutzer.buggy.developer.entity.Developer
import de.dkutzer.buggy.developer.entity.DeveloperCreatedEvent
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
    var createdEventEmitter: Emitter<AmqpMessage<String>>? = null

    @Outgoing("developer")
    @Incoming("developer")
    @Broadcast
    @Throws(JsonProcessingException::class)
    fun process(developer: String): org.eclipse.microprofile.reactive.messaging.Message<String> {
        val message1 = Message.Factory.create()
        message1.body = AmqpValue(developer)
        message1.contentType="application/json"
        message1.subject = "DeveloperCreated"
        message1.contentEncoding = Charsets.UTF_8.name()
        val amqpMessageImpl = AmqpMessageImpl(message1)


        val msg = AmqpMessage<String>(amqpMessageImpl)
        return msg
    }

    @Throws(JsonProcessingException::class)
    fun created(developer: Developer) {
        createdEventEmitter!!.send(AmqpMessage(objectMapper.writeValueAsString(DeveloperCreatedEvent(developer))))
    }
}