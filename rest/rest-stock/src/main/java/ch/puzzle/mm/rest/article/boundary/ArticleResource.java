package ch.puzzle.mm.rest.article.boundary;

import ch.puzzle.mm.rest.article.control.ArticleService;
import ch.puzzle.mm.rest.article.entity.Article;
import ch.puzzle.mm.rest.monkey.control.ChaosMonkey;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/articles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {

    @Inject
    ArticleService articleService;

    @GET
    @ChaosMonkey
    public List<Article> listAll() {
        return articleService.listAll();
    }

    @GET
    @Path("/{id}")
    @ChaosMonkey
    public Article get(@PathParam("id") Long id) {
        return articleService.getById(id);
    }
}
