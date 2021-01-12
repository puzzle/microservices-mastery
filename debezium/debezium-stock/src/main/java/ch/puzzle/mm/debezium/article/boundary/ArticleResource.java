package ch.puzzle.mm.debezium.article.boundary;

import ch.puzzle.mm.debezium.article.control.ArticleService;
import ch.puzzle.mm.debezium.article.entity.Article;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {

    @Inject
    ArticleService articleService;

    @GET
    public List<Article> listAll() {
        return articleService.listAll();
    }

    @GET
    @Path("/{id}")
    public Article get(@PathParam("id") Long id) {
        return articleService.getById(id);
    }
}
