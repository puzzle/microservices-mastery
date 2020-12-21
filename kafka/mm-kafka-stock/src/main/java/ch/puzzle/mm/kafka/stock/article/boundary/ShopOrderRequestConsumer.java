package ch.puzzle.mm.kafka.stock.article.boundary;

import ch.puzzle.mm.kafka.stock.exception.ArticleOutOfStockException;
import ch.puzzle.mm.kafka.stock.stock.control.ArticleStockService;
import ch.puzzle.mm.kafka.stock.stock.entity.ShopOrderDTO;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@Traced
public class ShopOrderRequestConsumer {

    private final Logger logger = LoggerFactory.getLogger(ShopOrderRequestConsumer.class.getName());

    @Inject
    @Channel("shop-order-confirmation")
    Emitter<ShopOrderDTO> articleStockConfirmationEmitter;

    @Inject
    @Channel("shop-order-compensation")
    Emitter<ShopOrderDTO> articleStockCompensationEmitter;

    @Inject
    Tracer tracer;

    @Inject
    ArticleStockService articleStockService;

    @Incoming("shop-order-request")
    public CompletionStage<Void> consumeOrders(KafkaRecord<String, ShopOrderDTO> message) {
        return CompletableFuture.runAsync(() -> {
            try (Scope scope = tracer.buildSpan("consume-order").asChildOf(TracingKafkaUtils.extractSpanContext(message.getHeaders(), tracer)).startActive(true)) {
                handleMessage(message);
            } catch (Exception e) {
                logger.info("OIOIdsofji");
            }
        }).thenRun(message::ack);
    }

    private void handleMessage(KafkaRecord<String, ShopOrderDTO> message) {
        try {
            articleStockService.orderArticles(message.getPayload().articleOrders);
            confirmShopOrder(message.getPayload());
        } catch (ArticleOutOfStockException e) {
            compensateOrder(message.getPayload());
        }
    }

    @Counted(name = "kafka_stock_order_compensated", absolute = true)
    @Outgoing("shop-order-compensation")
    private void compensateOrder(ShopOrderDTO shopOrderDTO) {
        articleStockCompensationEmitter.send(shopOrderDTO);
    }

    @Counted(name = "kafka_stock_order_confirmed", absolute = true)
    @Outgoing("shop-order-confirmation")
    private void confirmShopOrder(ShopOrderDTO shopOrderDTO) {
        articleStockConfirmationEmitter.send(shopOrderDTO);
    }
}
