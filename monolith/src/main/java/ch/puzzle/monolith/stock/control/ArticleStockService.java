package ch.puzzle.monolith.stock.control;

import ch.puzzle.monolith.monkey.control.ChaosMonkey;
import ch.puzzle.monolith.stock.entity.ArticleStock;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.opentracing.Traced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.resource.spi.ConfigProperty;

@ApplicationScoped
public class ArticleStockService {

    private final Logger log = LoggerFactory.getLogger(ArticleStockService.class.getName());

    @ChaosMonkey
    @CircuitBreaker(requestVolumeThreshold = 2)
    @Traced(operationName = "ArticleStockService::orderArticle")
    public void orderArticle(Long articleId, int amount) throws ArticleOutOfStockException {
        ArticleStock articleStock = ArticleStock.find("article_id", articleId).singleResult();
        if (articleStock.getCount() < amount)
            throw new ArticleOutOfStockException("Article with id " + articleId + " is out of stock.");
        articleStock.setCount(articleStock.getCount() - amount);
        log.info("Article with id {} processed", articleId);
    }
}
