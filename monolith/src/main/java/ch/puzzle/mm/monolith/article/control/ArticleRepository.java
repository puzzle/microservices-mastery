package ch.puzzle.mm.monolith.article.control;

import ch.puzzle.mm.monolith.article.entity.Article;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Traced
public class ArticleRepository implements PanacheRepository<Article> {
}
