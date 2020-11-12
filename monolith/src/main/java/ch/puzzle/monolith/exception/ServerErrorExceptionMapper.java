package ch.puzzle.monolith.exception;

import javax.json.Json;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServerErrorExceptionMapper implements ExceptionMapper<ServerErrorException> {

    @Override
    public Response toResponse(ServerErrorException exception) {
        return Response.status(500)
                .entity(Json.createObjectBuilder()
                        .add("message", exception.getMessage())
                        .build())
                .build();
    }
}
