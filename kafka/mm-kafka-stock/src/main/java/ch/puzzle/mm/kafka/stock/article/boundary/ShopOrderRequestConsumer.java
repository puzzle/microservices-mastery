package ch.puzzle.mm.kafka.stock.article.boundary;

import ch.puzzle.mm.kafka.stock.exception.ArticleOutOfStockException;
import ch.puzzle.mm.kafka.stock.stock.control.ArticleStockService;
import ch.puzzle.mm.kafka.stock.stock.entity.ShopOrderDTO;
import ch.puzzle.mm.kafka.stock.util.HeadersMapExtractAdapter;
import ch.puzzle.mm.kafka.stock.util.HeadersMapInjectAdapter;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
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
    public CompletionStage<Void> consumeOrders(Message<ShopOrderDTO> message) {
        logger.info("Message received");
        Optional<IncomingKafkaRecordMetadata> metadata = message.getMetadata(IncomingKafkaRecordMetadata.class);
        if (metadata.isPresent()) {
            SpanContext extract = tracer.extract(Format.Builtin.TEXT_MAP, new HeadersMapExtractAdapter(metadata.get().getHeaders()));
            try (Scope scope = tracer.buildSpan("consume-orders").asChildOf(extract).startActive(true)) {
                return handleMessage(message, metadata);
            }
        }
        return message.nack(new RuntimeException());
    }

    private CompletionStage<Void> handleMessage(Message<ShopOrderDTO> message, Optional<IncomingKafkaRecordMetadata> metadata) {
            ManagedExecutor executor = ManagedExecutor.builder()
                    .maxAsync(5)
                    .propagated(ThreadContext.CDI, ThreadContext.TRANSACTION)
                    .build();
            executor.runAsync(() -> {
                try {
                    articleStockService.orderArticles(message.getPayload().articleOrders);
                    confirmShopOrder(message.getPayload());
                } catch (ArticleOutOfStockException e) {
                    compensateOrder(message.getPayload());
                }
            });
        return message.ack();
    }

    @Counted(name = "kafka_stock_order_compensated", absolute = true)
    private void compensateOrder(ShopOrderDTO shopOrderDTO) {
        HeadersMapInjectAdapter headersMapInjectAdapter = new HeadersMapInjectAdapter();
        tracer.inject(tracer.scopeManager().active().span().context(), Format.Builtin.TEXT_MAP, headersMapInjectAdapter);
        OutgoingKafkaRecordMetadata outgoingKafkaRecordMetadata = OutgoingKafkaRecordMetadata.<ShopOrderDTO>builder()
                .withKey(shopOrderDTO)
                .withTopic("shop-order-compensation")
                .withHeaders(headersMapInjectAdapter.getRecordHeaders())
                .build();
        articleStockCompensationEmitter.send(Message.of(shopOrderDTO, Metadata.of(outgoingKafkaRecordMetadata)));
    }

    @Counted(name = "kafka_stock_order_confirmed", absolute = true)
    private void confirmShopOrder(ShopOrderDTO shopOrderDTO) {
        HeadersMapInjectAdapter headersMapInjectAdapter = new HeadersMapInjectAdapter();
        tracer.inject(tracer.scopeManager().active().span().context(), Format.Builtin.TEXT_MAP, headersMapInjectAdapter);
        OutgoingKafkaRecordMetadata outgoingKafkaRecordMetadata = OutgoingKafkaRecordMetadata.<ShopOrderDTO>builder()
                .withKey(shopOrderDTO)
                .withTopic("shop-order-confirmation")
                .withHeaders(headersMapInjectAdapter.getRecordHeaders())
                .build();
        articleStockConfirmationEmitter.send(Message.of(shopOrderDTO, Metadata.of(outgoingKafkaRecordMetadata)));
    }
}
