package de.dkutzer.buggy.developer.developer.control

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class UnknownEventException(private val entity: Any, private val eventName: String?) : RuntimeException() {
    override val message: String?
        get() = "not able to map ${entity.javaClass.simpleName} to eventName: $eventName"
}

