package de.dkutzer.buggy.developer.control

import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.http.MediaType
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
@EnableBinding(Source::class)
class MessageGateway(val source: Source) {

    interface Event

    fun send(event: Event) {
        val map = mapOf(
                "event" to event.javaClass.simpleName,
                "contentType" to MediaType.APPLICATION_JSON_UTF8_VALUE
        )
        val message = MessageBuilder.withPayload(event).copyHeaders(map).build()
        source.output().send(message)
    }
}

