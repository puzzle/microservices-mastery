package ch.puzzle.mm.rest.stock.boundary;

import ch.puzzle.mm.rest.stock.control.ArticleStockService;
import ch.puzzle.mm.rest.exception.ArticleOutOfStockException;
import ch.puzzle.mm.rest.monkey.control.ChaosMonkey;
import ch.puzzle.mm.rest.stock.entity.ArticleOrderDTO;
import ch.puzzle.mm.rest.stock.entity.ArticleStock;
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
    @ChaosMonkey
    public Response listAll() {
        return Response.ok(ArticleStock.listAll()).build();
    }

    @POST
    @ChaosMonkey
    @Counted(name = "rest_stock_reserve_plain_request", absolute = true, description = "number of stock reservations", tags = {"application=rest-stock", "resource=ArticleStockResource"})
    @Timed(name = "rest_stock_reserve_plain_timer", absolute = true, description = "timer for processing a stock reservation", tags = {"application=rest-stock", "resource=ArticleStockResource"})
    public Response createStockOrder(List<ArticleOrderDTO> articles) throws ArticleOutOfStockException {
        // handle stock count
        articleStockService.orderArticles(articles, "no-lra-version");

        return Response.ok().build();
    }
}