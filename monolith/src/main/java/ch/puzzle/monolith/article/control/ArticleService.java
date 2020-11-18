package ch.puzzle.monolith.article.control;

import ch.puzzle.monolith.article.entity.Article;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ArticleService {

    @Inject
    ArticleRepository articleRepository;

    public List<Article> listAll() {
        return articleRepository.listAll();
    }
}
