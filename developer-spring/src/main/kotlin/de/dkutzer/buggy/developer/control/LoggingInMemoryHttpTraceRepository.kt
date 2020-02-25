package de.dkutzer.buggy.developer.control

import mu.KotlinLogging
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.boot.actuate.trace.http.HttpTrace
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger {}
@Repository
class LoggingInMemoryHttpTraceRepository() : InMemoryHttpTraceRepository(){
    override fun add(trace: HttpTrace){
        super.add(trace)

        logger.info { "Request: ${trace.request.method} : ${trace.request.uri} - RespCode: ${trace.response.status} - ReqH:  ${trace.request.headers}  "}

        logger.info {  "Trace: ${ToStringBuilder.reflectionToString(trace)} "  }
        logger.info {  "Request: ${ToStringBuilder.reflectionToString(trace.request)} "  }
        logger.info {  "Response: ${ToStringBuilder.reflectionToString(trace.response)} "  }

    }
}