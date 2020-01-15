package de.dkutzer.buggy.developer.configuration

import com.google.common.collect.Maps
import io.quarkus.scheduler.Scheduled
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.core.MediaType


@ApplicationScoped
class HeartbeatScheduledController {

    data class StatusDTO(val name: String,val  managementUrl: String,val healthUrl: String,val serviceUrl:String,val  metaData: Map<String, String>)

    @Inject
    @ConfigProperty(name = "quarkus.application.name", defaultValue = "buggy-developer-service")
    lateinit var appName: String

    @Inject
    @ConfigProperty(name = "quarkus.application.uri", defaultValue = "http://buggy-developer-quarkus-service:8080/")
    lateinit var  appuri: String

    @Inject
    @field: RestClient
    internal lateinit var restClient: SpringBootAdminRestClient

    @Scheduled(every="10s")
    fun postStatusToSBA(){
        val statusDTO = StatusDTO(appName, appuri, appuri+"health", appuri, Maps.newHashMap())

        restClient.postStatus(MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON, statusDTO);



    }
}