package ch.puzzle.rest.stock.boundary;

import ch.puzzle.rest.article.entity.Article;
import ch.puzzle.rest.stock.control.ArticleOutOfStockException;
import ch.puzzle.rest.stock.control.ArticleStockService;
import ch.puzzle.rest.stock.entity.ArticleOrderDTO;
import ch.puzzle.rest.stock.entity.ShopOrderDTO;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/article-stocks")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleStockResource {

    @Inject
    ArticleStockService articleStockService;

    @POST
    @Transactional
    public Response createStockOrder(List<ArticleOrderDTO> articles) throws ArticleOutOfStockException {
        for (ArticleOrderDTO articleOrder : articles) {
            articleStockService.orderArticle(articleOrder.articleId, articleOrder.amount);
        }
        return Response.ok().build();
    }
}
