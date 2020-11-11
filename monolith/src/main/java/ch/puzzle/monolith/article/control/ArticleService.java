package ch.puzzle.monolith.article.control;

import ch.puzzle.monolith.orders.entity.Article;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ArticleService {

    public List<Article> listAll() {
        return Article.listAll();
    }
}
