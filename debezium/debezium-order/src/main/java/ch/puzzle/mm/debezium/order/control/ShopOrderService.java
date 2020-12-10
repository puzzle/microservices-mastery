package ch.puzzle.mm.debezium.order.control;

import ch.puzzle.mm.debezium.article.entity.Article;
import ch.puzzle.mm.debezium.event.OrderCreatedEvent;
import ch.puzzle.mm.debezium.order.entity.ShopOrder;
import ch.puzzle.mm.debezium.order.entity.ShopOrderDTO;
import ch.puzzle.mm.debezium.order.entity.ShopOrderStatus;
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
}
