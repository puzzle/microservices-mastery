package ch.puzzle.mm.debezium.order.control;

import ch.puzzle.mm.debezium.event.entity.OrderCancelledEvent;
import ch.puzzle.mm.debezium.event.entity.OrderCreatedEvent;
import ch.puzzle.mm.debezium.order.entity.*;
import io.debezium.outbox.quarkus.ExportedEvent;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Traced
@ApplicationScoped
public class ShopOrderService {

    public List<ShopOrder> listAll() {
        return ShopOrder.listAll();
    }

    @Inject
    Event<ExportedEvent<?, ?>> event;

    public ShopOrder createOrder(ShopOrderDTO shopOrderDTO) {
        List<ArticleOrder> articleOrders = shopOrderDTO.articleOrders.stream().map(s -> new ArticleOrder(s.articleId, s.amount)).collect(Collectors.toList());

        // store order to shopOrder table
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setStatus(ShopOrderStatus.NEW);
        shopOrder.setArticleOrders(articleOrders);
        shopOrder.persist();

        // fire event (outbox table)
        event.fire(new OrderCreatedEvent(Instant.now(), shopOrder));

        return shopOrder;
    }

    @Counted(name = "debezium_order_stockevent_complete", absolute = true, description = "number of stockcomplete events from stock", tags = {"application=debezium-order", "resource=ShopOrderService"})
    public void onStockCompleteEvent(ShopOrderStockResponse stockComplete) {
        ShopOrder.findByIdOptional(stockComplete.orderId).ifPresent(o -> {
            ((ShopOrder) o).setStatus(ShopOrderStatus.COMPLETED);
        });
    }

    @Counted(name = "debezium_order_stockevent_incomplete", absolute = true, description = "number of stockincomplete events from stock", tags = {"application=debezium-order", "resource=ShopOrderService"})
    public void onStockIncompleteEvent(ShopOrderStockResponse stockIncomplete) {
        ShopOrder.findByIdOptional(stockIncomplete.orderId).ifPresent(o -> {
            ((ShopOrder) o).setStatus(ShopOrderStatus.STOCK_INCOMPLETE);
        });
    }

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
