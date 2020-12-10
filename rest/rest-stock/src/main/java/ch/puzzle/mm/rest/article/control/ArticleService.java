package ch.puzzle.mm.rest.article.control;

import ch.puzzle.mm.rest.article.entity.Article;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
@Traced
public class ArticleService {

    @Inject
    ArticleRepository articleRepository;

    public List<Article> listAll() {
        return articleRepository.listAll();
    }
}
