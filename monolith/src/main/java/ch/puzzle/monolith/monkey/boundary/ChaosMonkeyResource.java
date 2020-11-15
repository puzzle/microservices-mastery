package ch.puzzle.monolith.monkey.boundary;

import ch.puzzle.monolith.monkey.control.ChaosMonkeyService;
import ch.puzzle.monolith.monkey.entity.MonkeyConfig;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/chaos-monkey")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChaosMonkeyResource {

    @Inject
    ChaosMonkeyService monkeyService;

    @GET
    public Response list() {
        return Response.ok(monkeyService.getFullConfig()).build();
    }

    @PUT
    public void update(MonkeyConfig config) {
        monkeyService.addMonkeyConfig(config, null);
    }

    @PUT
    @Path("/{class}")
    public void createSpecific(@PathParam("class") String clazz, MonkeyConfig config) {
        monkeyService.addMonkeyConfig(config, clazz);
    }
}