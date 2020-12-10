package ch.puzzle.mm.monolith.exception;

import javax.json.Json;
import javax.persistence.EntityExistsException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityExistsExceptionMapper implements ExceptionMapper<EntityExistsException> {

    @Override
    public Response toResponse(EntityExistsException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Json.createObjectBuilder()
                        .add("message", exception.getMessage())
                        .build())
                .build();
    }
}
