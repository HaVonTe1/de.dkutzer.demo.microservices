package de.dkutzer.buggy.developer.configuration


import io.quarkus.scheduler.Scheduled
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.core.MediaType


@ApplicationScoped
class HeartbeatScheduledController {

    data class StatusDTO(val name: String, val managementUrl: String, val healthUrl: String, val serviceUrl: String, val metaData: Map<String, String>)

    @Inject
    @ConfigProperty(name = "quarkus.application.name", defaultValue = "buggy-developer-service")
    lateinit var appName: String

    @Inject
    @ConfigProperty(name = "quarkus.application.uri", defaultValue = "http://buggy-developer-quarkus-service:8080/")
    lateinit var appuri: String
    @Inject
    @ConfigProperty(name = "quarkus.application.sba.enabled", defaultValue = "true")
    lateinit var sbaEnabled: String

    @Inject
    @field: RestClient
    internal lateinit var restClient: SpringBootAdminRestClient

    @Scheduled(every = "10s")
    fun postStatusToSBA() {
        if(sbaEnabled.compareTo("true")==0){
            val statusDTO = StatusDTO(appName, appuri, appuri + "health", appuri, HashMap())
            restClient.postStatus(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, statusDTO);

        }

        //TODO: add de-register
    }
}