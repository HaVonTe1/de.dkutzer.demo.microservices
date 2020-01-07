package de.dkutzer.buggy.developer.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.dkutzer.buggy.developer.control.DeveloperService;
import de.dkutzer.buggy.developer.entity.Developer;
import io.quarkus.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/developers")
@Authenticated
public class DeveloperController {

    @Inject
    DeveloperService developerService;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ROLE_BUGGY_UI"})
    public Iterable<Developer> findAll(){
        return developerService.findAll();
    }

    @GET()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ROLE_BUGGY_UI"})
    public Response findById(@PathParam("id") String id){
        return Response.ok(developerService.findById(id)).build();
    }

    @DELETE()
    @Path("/{id}")
    @RolesAllowed({"ROLE_BUGGY_UI"})
    public Response delete(@PathParam("id") String id) {
        return Response.status(developerService.deleteById(id)? Response.Status.GONE: Response.Status.NOT_FOUND).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Developer developer) throws JsonProcessingException {
        String id = developer.getId();
        Developer entity = developerService.upsert(developer);
        if (developerService.exists(id)) {

            return Response.accepted(entity).build();
        } else {

            return Response.created(UriBuilder.fromPath("/" + entity.getId()).build()).build();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response update(@PathParam("id") String id,Developer developer) throws JsonProcessingException {

        if(developerService.exists(id)){
            developer.setId(id);
            return Response.accepted(developerService.upsert(developer)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }



}