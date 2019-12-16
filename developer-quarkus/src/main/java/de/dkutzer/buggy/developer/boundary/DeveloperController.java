package de.dkutzer.buggy.developer.boundary;

import de.dkutzer.buggy.developer.control.DeveloperRepository;
import de.dkutzer.buggy.developer.control.DeveloperService;
import de.dkutzer.buggy.developer.entity.Developer;
import io.quarkus.security.Authenticated;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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




}