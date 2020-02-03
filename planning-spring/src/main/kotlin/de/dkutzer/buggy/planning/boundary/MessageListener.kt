package de.dkutzer.buggy.planning.boundary

import de.dkutzer.buggy.planning.control.DeveloperServices
import de.dkutzer.buggy.planning.control.IssuesServices
import de.dkutzer.buggy.planning.entity.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.binder.BinderHeaders
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.processing.Processor
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType


private val logger = KotlinLogging.logger {}
@Service
@EnableBinding(DevelopersChannel::class)
class DevelopersMessageListener(val developerService: DeveloperServices, val issuesService: IssuesServices) {

    @StreamListener(target = DevelopersChannel.INPUT, condition = "headers['event']=='DeveloperCreated'")
    fun handleDeveloperCreatedEvent(developerCreated: DeveloperCreated) {
        developerService.upsert(developerCreated)
    }

    @StreamListener(target = DevelopersChannel.INPUT, condition = "headers['event']=='DeveloperUpdated'")
    fun handleDeveloperUpdatedEvent(developerUpdated: DeveloperUpdated) {
        developerService.upsert(developerUpdated)
    }

    @StreamListener(target = DevelopersChannel.INPUT, condition = "headers['event']=='DeveloperDeleted'")
    fun handleDeveloperDeletedEvent(developerDeleted: DeveloperDeleted) {
        developerService.delete(developerDeleted)
    }

}

@Service
@EnableBinding(IssuesChannel::class)
class IssuesMessageListener(val issuesService: IssuesServices) {

    @StreamListener(target = IssuesChannel.INPUT, condition = "headers['event']=='IssueCreated'")
    fun handleIssueCreatedEvent(issueCreated: IssueCreated) {
        throw IllegalArgumentException("test")
//        issuesService.upsert(issueCreated)
    }

    @StreamListener(target = IssuesChannel.INPUT, condition = "headers['event']=='IssueUpdated'")
    fun handleIssueUpdatedEvent(issueUpdated: IssueUpdated) {
        issuesService.upsert(issueUpdated)
    }

    @StreamListener(target = IssuesChannel.INPUT, condition = "headers['event']=='IssueDeleted'")
    fun handleIssueDeletedEvent(issueDeleted: IssueDeleted) {
        issuesService.delete(issueDeleted)
    }

}


@Service
@EnableBinding(DevelopersErrorChannel::class, IssuesErrorChannel::class, DevelopersParkingLog::class, IssuesParkingLog::class)
class ReRouteDlqMessageListener(val developersParkingLog: DevelopersParkingLog, val issuesParkingLog: IssuesParkingLog){


    @StreamListener("developers_error_channel_in")
    @SendTo("developers_error_channel_out")
    fun reRouteDev(failed : Message<Any>): Message<Any>? = reRoute(failed, developersParkingLog)

    @StreamListener("issues_error_channel_in")
    @SendTo("issues_error_channel_out")
    fun reRouteIssues(failed : Message<Any>): Message<Any>? = reRoute(failed, issuesParkingLog)

    fun reRoute(failed : Message<Any>, errorChannel: ParkingLotChannel ): Message<Any>?{
        val retries = failed.headers.getOrDefault("x-retries",0) as Int
        if (retries < 3){
            logger.debug {"Retry $retries for $failed"}
            return MessageBuilder.fromMessage(failed).setHeader("x-retries", retries+1).setHeader(BinderHeaders.PARTITION_OVERRIDE, failed.headers[KafkaHeaders.RECEIVED_PARTITION_ID]).build()

        }else {
            logger.debug { " Retries exhausted for $failed"}
            errorChannel.parkingLot().send(MessageBuilder.fromMessage(failed).setHeader(BinderHeaders.PARTITION_OVERRIDE, failed.headers[KafkaHeaders.RECEIVED_PARTITION_ID]).build())
        }
        return null

    }
}

