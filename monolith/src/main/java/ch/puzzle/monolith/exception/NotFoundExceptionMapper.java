package ch.puzzle.monolith.exception;

import javax.json.Json;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

    @Override
    public Response toResponse(EntityNotFoundException exception) {
        return Response.status(404)
                .entity(Json.createObjectBuilder()
                        .add("message", exception.getMessage())
                        .build())
                .build();
    }
}
