package ch.puzzle.mm.debezium.stock.control;

import ch.puzzle.mm.debezium.event.entity.StockCompleteEvent;
import ch.puzzle.mm.debezium.event.entity.StockIncompleteEvent;
import ch.puzzle.mm.debezium.stock.entity.ArticleStock;
import ch.puzzle.mm.debezium.stock.entity.Order;
import ch.puzzle.mm.debezium.stock.entity.OrderArticle;
import io.debezium.outbox.quarkus.ExportedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ArticleStockService {

    private final Logger logger = LoggerFactory.getLogger(ArticleStockService.class.getName());

    @Inject
    Event<ExportedEvent<?, ?>> event;

    public void orderCreated(Order order) {
        logger.info("Processing 'OrderCreated' event: {}", order.orderId);

        List<Long> ids = order.items.stream().map(OrderArticle::getArticleId).collect(Collectors.toList());
        List<ArticleStock> articleStockList = ArticleStock.list("id in ?1", ids);
        Map<Long, ArticleStock> stock = articleStockList.stream().collect(Collectors.toMap(x -> x.getArticle().id, x -> x));

        if (isStockComplete(stock, order.items)) {
            // reduce stock count
            for (OrderArticle item : order.items) {
                ArticleStock as = stock.get(item.articleId);
                as.setCount(as.getCount() - item.getAmount());
            }

            event.fire(new StockCompleteEvent(Instant.now(), order.orderId));
        } else {
            event.fire(new StockIncompleteEvent(Instant.now(), order.orderId));
        }
    }

    boolean isStockComplete(Map<Long, ArticleStock> stock, List<OrderArticle> items) {
        for (OrderArticle item : items) {
            if (!stock.containsKey(item.articleId) || stock.get(item.articleId).getCount() < item.getAmount()) {
                return false;
            }
        }

        return true;
    }

    public void orderCanceled(Order order) {
        logger.info("Processing 'OrderCancelled' event: {}", order.orderId);

        order.items.forEach(item -> {
            ArticleStock as = ArticleStock.findByArticleId(item.articleId);
            as.setCount(as.getCount() + item.getAmount());
        });
    }
}
