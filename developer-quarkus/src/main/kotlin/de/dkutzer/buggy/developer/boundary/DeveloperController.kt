package de.dkutzer.buggy.developer.boundary

import com.fasterxml.jackson.core.JsonProcessingException
import de.dkutzer.buggy.developer.control.DeveloperService
import de.dkutzer.buggy.developer.entity.Developer
import io.quarkus.security.Authenticated
import javax.annotation.security.RolesAllowed
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder

@Path("/developers")
@Authenticated
class DeveloperController {


    @Inject
    @field: Default
    lateinit var developerService: DeveloperService


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ROLE_BUGGY_UI")
    fun findAll(): Iterable<Developer> {
        return developerService.findAll()
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ROLE_BUGGY_UI")
    fun findById(@PathParam("id") id: String): Response {
        return Response.ok(developerService.findById(id)).build()
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ROLE_BUGGY_UI")
    fun delete(@PathParam("id") id: String): Response {
        return Response.status(if (developerService.deleteById(id)) Response.Status.GONE else Response.Status.NOT_FOUND).build()
    }

    @DELETE
    @RolesAllowed("ROLE_BUGGY_UI")
    fun deleteAll(): Response {
        this.developerService.deleteAll();
        return Response.status(Response.Status.GONE).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Throws(JsonProcessingException::class)
    fun create(developer: Developer): Response {
        val id = developer.id
        val entity = developerService.upsert(developer)
        return if (developerService.exists(id)) {
            Response.accepted(entity).build()
        } else {
            Response.created(UriBuilder.fromPath("/developers/" + entity.id).build()).build()
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Throws(JsonProcessingException::class)
    fun update(@PathParam("id") id: String, developer: Developer): Response {
        if (developerService.exists(id)) {

            return Response.accepted(developerService.upsert(developer)).build()
        }
        return Response.status(Response.Status.NOT_FOUND).build()
    }
}