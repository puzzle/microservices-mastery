package ch.puzzle.mm.debezium.stock.boundary;

import ch.puzzle.mm.debezium.stock.control.ArticleStockService;
import ch.puzzle.mm.debezium.stock.entity.ArticleStock;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/article-stocks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticleStockResource {

    private final Logger log = LoggerFactory.getLogger(ArticleStockResource.class.getName());

    @Inject
    ArticleStockService articleStockService;

    @GET
    public Response listAll() {
        return Response.ok(ArticleStock.listAll()).build();
    }
}