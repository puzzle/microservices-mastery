package ch.puzzle.mm.monolith.article.control;

import ch.puzzle.mm.monolith.article.entity.Article;

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

    public Article findById(Long articleId) {
        return articleRepository.findById(articleId);
    }
}
