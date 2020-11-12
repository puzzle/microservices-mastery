package ch.puzzle.monolith.stock.control;

import ch.puzzle.monolith.stock.entity.ArticleStock;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArticleStockService {
    public void orderArticle(Long articleId) throws ArticleOutOfStockException {
        ArticleStock articleStock = ArticleStock.find("article_id", articleId).singleResult();
        if (articleStock.getCount() == 0)
            throw new ArticleOutOfStockException("Article with id " + articleId + " is out of stock.");
        articleStock.setCount(articleStock.getCount() - 1);
    }
}
