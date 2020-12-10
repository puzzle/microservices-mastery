package ch.puzzle.kafka.stock.boundary;

import ch.puzzle.kafka.exception.ArticleOutOfStockException;
import ch.puzzle.kafka.order.entity.ShopOrder;
import ch.puzzle.kafka.order.entity.ShopOrderDTO;
import ch.puzzle.kafka.stock.control.ArticleStockService;
import ch.puzzle.kafka.stock.entity.ArticleStockRequest;
import ch.puzzle.kafka.util.HeadersMapExtractAdapter;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ArticleStockRequestConsumer {

    @Inject
    @Channel("article-stock-confirmed")
    Emitter<ShopOrderDTO> articleStockConfirmationEmitter;

    @Inject
    @Channel("article-stock-compensation")
    Emitter<ShopOrderDTO> articleStockCompensationEmitter;

    @Inject
    Tracer tracer;

    @Inject
    ArticleStockService articleStockService;

    @Incoming("article-stock-request")
    public CompletionStage<Void> consumeOrders(Message<ShopOrderDTO> message) {
        Optional<IncomingKafkaRecordMetadata> metadata = message.getMetadata(IncomingKafkaRecordMetadata.class);
        if (metadata.isPresent()) {
            SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new HeadersMapExtractAdapter(metadata.get().getHeaders()));
            try (Scope scope = tracer.buildSpan("consume-orders").asChildOf(spanContext).startActive(true)) {
                articleStockService.orderArticles(message.getPayload().articleOrders);
                confirmShopOrder(message.getPayload(), scope);
                return message.ack();
            } catch (ArticleOutOfStockException e) {
                compensateOrder(message.getPayload(), tracer.buildSpan("consume-orders").asChildOf(spanContext).startActive(true));
                return message.nack(e);
            }
        }
        return message.nack(new RuntimeException());
    }

    private void compensateOrder(ShopOrderDTO shopOrderDTO, Scope scope) {
        HeadersMapExtractAdapter headersMapExtractAdapter = new HeadersMapExtractAdapter();
        tracer.inject(scope.span().context(), Format.Builtin.TEXT_MAP, headersMapExtractAdapter);
        OutgoingKafkaRecordMetadata outgoingKafkaRecordMetadata = OutgoingKafkaRecordMetadata.<ShopOrderDTO>builder()
                .withKey(shopOrderDTO)
                .withTopic("manual")
                .withHeaders(headersMapExtractAdapter.getRecordHeaders())
                .build();
        articleStockCompensationEmitter.send(Message.of(shopOrderDTO, Metadata.of(outgoingKafkaRecordMetadata)));
    }

    private void confirmShopOrder(ShopOrderDTO shopOrderDTO, Scope scope) {
        HeadersMapExtractAdapter headersMapExtractAdapter = new HeadersMapExtractAdapter();
        tracer.inject(scope.span().context(), Format.Builtin.TEXT_MAP, headersMapExtractAdapter);
        OutgoingKafkaRecordMetadata outgoingKafkaRecordMetadata = OutgoingKafkaRecordMetadata.<ShopOrderDTO>builder()
                .withKey(shopOrderDTO)
                .withTopic("manual")
                .withHeaders(headersMapExtractAdapter.getRecordHeaders())
                .build();
        articleStockConfirmationEmitter.send(Message.of(shopOrderDTO, Metadata.of(outgoingKafkaRecordMetadata)));
    }
}
