package ch.puzzle.mm.kafka.stock.exception;

import javax.json.Json;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ArticleOutOfStockExceptionMapper implements ExceptionMapper<ArticleOutOfStockException> {

    @Override
    public Response toResponse(ArticleOutOfStockException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(Json.createObjectBuilder()
                        .add("message", exception.getMessage())
                        .build())
                .build();
    }
}
