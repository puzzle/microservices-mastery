package ch.puzzle.monolith.stock.boundary;

import ch.puzzle.monolith.orders.entity.Article;
import ch.puzzle.monolith.stock.entity.ArticleStock;
import ch.puzzle.monolith.stock.entity.ArticleStockDTO;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/article-stocks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticleStockResource {

    @GET
    public List<ArticleStock> listAll() {
        return ArticleStock.listAll();
    }

    @POST
    @Transactional
    public Response createArticleStock(ArticleStockDTO articleStockDTO) {
        ArticleStock articleStock = new ArticleStock(Article.findById(articleStockDTO.articleId), articleStockDTO.count);
        articleStock.persist();
        return Response.ok(articleStock).build();
    }
}
