package ch.puzzle.mm.monolith.exception;

import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;

import javax.json.Json;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CircuitBreakerOpenExceptionMapper implements ExceptionMapper<CircuitBreakerOpenException> {

    @Override
    public Response toResponse(CircuitBreakerOpenException exception) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(Json.createObjectBuilder()
                        .add("message", exception.getMessage())
                        .build())
                .build();
    }
}
