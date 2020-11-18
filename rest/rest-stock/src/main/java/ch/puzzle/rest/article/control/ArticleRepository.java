package ch.puzzle.rest.article.control;

import ch.puzzle.rest.article.entity.Article;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Traced
public class ArticleRepository implements PanacheRepository<Article> {
}
