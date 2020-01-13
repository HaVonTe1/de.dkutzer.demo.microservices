package de.dkutzer.buggy.planning.boundary

import io.smallrye.reactive.messaging.amqp.AmqpMessage
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletionStage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DeveloperMessageListener {

    private val log: Logger = LoggerFactory.getLogger(DeveloperMessageListener::class.java)


    @Incoming("developer")
    fun consume(msg: AmqpMessage<String?>): CompletionStage<Void?>? {
        log.debug("Received Msg from 'developer' channel")
        log.trace("Headers: {}", msg.header)
        log.trace("Address: {}", msg.address)
        log.trace("Props: {}", msg.applicationProperties)
        log.trace("Subject: {}", msg.subject)
        log.trace("body: {}", msg.body)
        log.trace("contentType: {}", msg.contentType)
        log.trace("Payload: {}", msg.payload)
        return msg.ack()
    }

}