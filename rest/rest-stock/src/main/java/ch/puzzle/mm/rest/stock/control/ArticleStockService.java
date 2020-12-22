package ch.puzzle.mm.rest.stock.control;

import ch.puzzle.mm.rest.exception.ArticleOutOfStockException;
import ch.puzzle.mm.rest.stock.entity.ArticleOrderDTO;
import ch.puzzle.mm.rest.stock.entity.ArticleStock;
import ch.puzzle.mm.rest.stock.entity.ArticleStockChange;
import org.eclipse.microprofile.opentracing.Traced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@Traced
@ApplicationScoped
public class ArticleStockService {

    private final Logger log = LoggerFactory.getLogger(ArticleStockService.class.getName());

    public void orderArticles(List<ArticleOrderDTO> articles, String lraId) throws ArticleOutOfStockException {
        for (ArticleOrderDTO articleOrder : articles) {
            ArticleStock articleStock = ArticleStock.findById(articleOrder.articleId);

            int preOrderAmount = articleStock.getCount();
            if (articleStock.getCount() < articleOrder.amount) {
                throw new ArticleOutOfStockException("Article with id " + articleOrder.articleId + " is out of stock.");
            }

            // handling change log
            ArticleStockChange asc = new ArticleStockChange(articleStock.getArticle(), articleOrder.amount, lraId);
            asc.persistAndFlush();

            // handing inventory count
            articleStock.setCount(articleStock.getCount() - articleOrder.amount);
            log.info("Article with id {} processed. Stock oldCount={}, ordered={}, newCount={}", articleOrder.articleId, preOrderAmount, articleOrder.amount, articleStock.getCount());
        }
    }
}