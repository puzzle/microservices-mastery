package ch.puzzle.mm.rest.article.boundary;

import ch.puzzle.mm.rest.article.control.ArticleService;
import ch.puzzle.mm.rest.article.entity.Article;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/articles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {

    @Inject
    ArticleService articleService;

    @GET
    public List<Article> listAll() {
        return articleService.listAll();
    }
}
