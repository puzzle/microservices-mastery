package ch.puzzle.mm.rest.stock.boundary;

import ch.puzzle.mm.rest.exception.ArticleOutOfStockException;
import ch.puzzle.mm.rest.monkey.control.ChaosMonkey;
import ch.puzzle.mm.rest.stock.control.ArticleStockService;
import ch.puzzle.mm.rest.stock.entity.ArticleOrderDTO;
import ch.puzzle.mm.rest.stock.entity.ArticleStock;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ParticipantStatus;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/lra/article-stocks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticleStockLraResource {

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
    @Counted(name = "rest_stock_reserve_request", absolute = true, description = "number of stock reservations", tags = {"application=rest-stock", "resource=ArticleStockLraResource"})
    @Timed(name = "rest_stock_reserve_timer", absolute = true, description = "timer for processing a stock reservation", tags = {"application=rest-stock", "resource=ArticleStockLraResource"})
    @LRA(value = LRA.Type.MANDATORY, end = false)
    public Response createStockOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) String lraUrl,
                                     List<ArticleOrderDTO> articles) throws ArticleOutOfStockException {

        String lraId = getLraId(lraUrl);

        log.info("----------------------------");
        log.info("Create ArticleOrder: {}", lraId);
        log.info("LRA Transaction Id: {}", lraId);
        log.info("----------------------------");

        // handle stock count
        articleStockService.orderArticles(articles, lraId);

        return Response.ok().build();
    }

    @PUT
    @Path("/complete")
    @Complete
    @Consumes(MediaType.TEXT_PLAIN)
    public Response completeShopOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) String lraUrl) {
        String lraId = getLraId(lraUrl);
        log.info("----------------------------");
        log.info("Complete ArticleOrder");
        log.info("LRA Transaction Id: {}", lraId);
        log.info("----------------------------");
        return Response.ok(ParticipantStatus.Completed.name()).build();
    }

    @PUT
    @Path("/compensate")
    @Compensate
    @Consumes(MediaType.TEXT_PLAIN)
    public Response compensateShopOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) String lraUrl) {
        String lraId = getLraId(lraUrl);
        log.info("----------------------------");
        log.info("Compensate ArticleOrder");
        log.info("LRA Transaction Id: {}", lraId);
        log.info("----------------------------");

        articleStockService.releaseOrderArticles(lraId);

        return Response.ok(ParticipantStatus.Compensated.name()).build();
    }

    String getLraId(String longRunningHeader) {
        return longRunningHeader.substring(longRunningHeader.lastIndexOf('/') + 1);
    }
}