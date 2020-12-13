package ch.puzzle.mm.debezium.order.control;

import ch.puzzle.mm.debezium.article.entity.Article;
import ch.puzzle.mm.debezium.event.entity.OrderCancelledEvent;
import ch.puzzle.mm.debezium.event.entity.OrderCreatedEvent;
import ch.puzzle.mm.debezium.order.entity.ShopOrder;
import ch.puzzle.mm.debezium.order.entity.ShopOrderDTO;
import ch.puzzle.mm.debezium.order.entity.ShopOrderStatus;
import ch.puzzle.mm.debezium.order.entity.ShopOrderStockResponse;
import io.debezium.outbox.quarkus.ExportedEvent;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ShopOrderService {

    public List<ShopOrder> listAll() {
        return ShopOrder.listAll();
    }

    @Inject
    Event<ExportedEvent<?, ?>> event;

    @Traced
    public ShopOrder createOrder(ShopOrderDTO shopOrderDTO) {
        List<Long> ids = shopOrderDTO.articleOrders.stream().map(s -> s.articleId).collect(Collectors.toList());
        List<Article> articles = Article.list("id in ?1", ids);

        // store order to shopOrder table
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setStatus(ShopOrderStatus.NEW);
        shopOrder.setArticles(articles);
        shopOrder.persist();

        // fire event (outbox table)
        event.fire(new OrderCreatedEvent(Instant.now(), shopOrder));

        return shopOrder;
    }

    @Traced
    public void onStockCompleteEvent(ShopOrderStockResponse stockComplete) {
        ShopOrder.findByIdOptional(stockComplete.orderId).ifPresent(o -> {
            ((ShopOrder) o).setStatus(ShopOrderStatus.COMPLETED);
        });
    }

    @Traced
    public void onStockIncompleteEvent(ShopOrderStockResponse stockIncomplete) {
        ShopOrder.findByIdOptional(stockIncomplete.orderId).ifPresent(o -> {
            ((ShopOrder) o).setStatus(ShopOrderStatus.STOCK_INCOMPLETE);
        });
    }

    @Traced
    public ShopOrder cancelOrder(long orderId) {
        ShopOrder order = ShopOrder.getByIdOrThrow(orderId);
        if (order.getStatus().canCancel()) {
            order.setStatus(ShopOrderStatus.CANCELLED);
            event.fire(new OrderCancelledEvent(Instant.now(), order));
            return order;
        } else {
            throw new IllegalStateException("Cannot cancel Order " + orderId);
        }
    }
}
