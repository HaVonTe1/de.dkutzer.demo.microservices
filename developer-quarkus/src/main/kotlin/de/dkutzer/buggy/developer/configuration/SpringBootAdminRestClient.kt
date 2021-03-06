package de.dkutzer.buggy.developer.configuration

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.core.Response


@RegisterRestClient(configKey = "sba-api")
interface SpringBootAdminRestClient {

    @POST
    @Produces("application/json")
    fun postStatus(@HeaderParam("accept") acceptHeader: String,
                   @HeaderParam("contentType") contentTypeHeader: String,
                   statusDTO: HeartbeatScheduledController.StatusDTO): Response

}