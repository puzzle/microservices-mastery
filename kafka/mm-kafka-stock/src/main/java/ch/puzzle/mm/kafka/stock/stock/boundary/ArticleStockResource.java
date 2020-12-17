package ch.puzzle.mm.kafka.stock.stock.boundary;

import ch.puzzle.mm.kafka.stock.exception.ArticleOutOfStockException;
import ch.puzzle.mm.kafka.stock.monkey.control.ChaosMonkey;
import ch.puzzle.mm.kafka.stock.stock.control.ArticleStockService;
import ch.puzzle.mm.kafka.stock.stock.entity.ArticleOrderDTO;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/article-stocks")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleStockResource {

    @Inject
    ArticleStockService articleStockService;

    @ChaosMonkey
    @POST
    @Transactional
    public Response createStockOrder(List<ArticleOrderDTO> articles) throws ArticleOutOfStockException {
        articleStockService.orderArticles(articles);
        return Response.ok().build();
    }
}
