package ch.puzzle.mm.rest.stock.boundary;

import ch.puzzle.mm.rest.util.ForceFail;
import ch.puzzle.mm.rest.exception.ArticleOutOfStockException;
import ch.puzzle.mm.rest.monkey.control.ChaosMonkey;
import ch.puzzle.mm.rest.stock.entity.ArticleOrderDTO;
import ch.puzzle.mm.rest.stock.entity.ArticleStock;
import ch.puzzle.mm.rest.stock.entity.ArticleStockChange;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ParticipantStatus;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/article-stocks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticleStockResource {

    private final Logger log = LoggerFactory.getLogger(ArticleStockResource.class.getName());

    @POST
    @ChaosMonkey
    @Transactional
    @LRA(value = LRA.Type.MANDATORY, end = false)
    public Response createStockOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) String lraUrl,
                                     @HeaderParam(LRA.LRA_HTTP_RECOVERY_HEADER) String recoveryHeader,
                                     @QueryParam("fail") ForceFail fail,
                                     List<ArticleOrderDTO> articles) throws ArticleOutOfStockException {

        String lraId = getLraId(lraUrl);
        log.info("----------------------------");
        log.info("Create ArticleOrder: {}", lraId);
        log.info("LRA Transaction Id: {}", lraId);
        log.info("----------------------------");

        for (ArticleOrderDTO articleOrder : articles) {
            ArticleStock articleStock = ArticleStock.findById(articleOrder.articleId);

            int preOrderAmount = articleStock.getCount();
            if (articleStock.getCount() < articleOrder.amount) {
                throw new ArticleOutOfStockException("Article with id " + articleOrder.articleId + " is out of stock.");
            }

            // handling change log
            new ArticleStockChange(articleStock.getArticle(), articleOrder.amount, lraId).persist();

            // handing inventory count
            articleStock.setCount(articleStock.getCount() - articleOrder.amount);
            log.info("Article with id {} processed. Stock oldCount={}, ordered={}, newCount={}", articleOrder.articleId, preOrderAmount, articleOrder.amount, articleStock.getCount());
        }

        if(fail == ForceFail.STOCK) {
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        } else {
            return Response.ok().build();
        }
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
    @Transactional
    @Compensate
    @Consumes(MediaType.TEXT_PLAIN)
    public Response compensateShopOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) String lraUrl) {
        String lraId = getLraId(lraUrl);
        log.info("----------------------------");
        log.info("Compensate ArticleOrder");
        log.info("LRA Transaction Id: {}", lraId);
        log.info("----------------------------");

        ArticleStockChange.findByLraId(lraId).forEach( asc -> {
            ArticleStock stock = ArticleStock.findByArticleId(asc.getArticle().id);
            int oldStockCount = stock.getCount();
            stock.setCount(stock.getCount() + asc.getCount());
            log.info("Compensating for Article {}: oldCount={}, release={}, newCount={}", stock.getArticle().id, oldStockCount, asc.getCount(), stock.getCount());
        });

        return Response.ok(ParticipantStatus.Compensated.name()).build();
    }

    String getLraId(String longRunningHeader) {
        return longRunningHeader.substring(longRunningHeader.lastIndexOf('/') + 1);
    }
}