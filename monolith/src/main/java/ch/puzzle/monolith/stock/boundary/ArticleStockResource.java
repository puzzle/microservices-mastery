package ch.puzzle.monolith.stock.boundary;

import ch.puzzle.monolith.article.entity.Article;
import ch.puzzle.monolith.stock.control.ArticleStockService;
import ch.puzzle.monolith.stock.entity.ArticleStock;
import ch.puzzle.monolith.stock.entity.ArticleStockDTO;
import ch.puzzle.monolith.stock.entity.StockUpdateDTO;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/article-stocks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticleStockResource {

    @Inject
    ArticleStockService articleStockService;

    @GET
    public List<ArticleStock> list() {
        return ArticleStock.listAll();
    }

    @POST
    @Counted(name = "monolith_stock_create", absolute = true)
    @Transactional
    public Response create(ArticleStockDTO articleStockDTO) {
        if (articleStockService.findById(articleStockDTO.articleId).isPresent()) {
            throw new EntityExistsException("ArticleStock with id " + articleStockDTO.articleId + " already exists.");
        }
        Article article = Article.findById(articleStockDTO.articleId);
        if (article == null) {
            throw new EntityNotFoundException("Article " + articleStockDTO.articleId + " not found");
        }

        ArticleStock articleStock = new ArticleStock(article, articleStockDTO.count);
        articleStock.persist();
        return Response.ok(articleStock).build();
    }

    @PUT
    @Path("/{id}")
    @Counted(name = "monolith_stock_update", absolute = true)
    @Transactional
    public Response update(@PathParam("id") Long id, StockUpdateDTO dto) {
        ArticleStock stock = ArticleStock.findById(id);
        if (stock == null) {
            throw new EntityNotFoundException("ArticleStock " + id + " not found");
        }

        stock.setCount(dto.count);
        stock.persist();
        return Response.ok(stock).build();
    }
}
