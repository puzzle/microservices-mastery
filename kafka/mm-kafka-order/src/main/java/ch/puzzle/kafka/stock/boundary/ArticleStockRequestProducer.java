package ch.puzzle.kafka.stock.boundary;

import ch.puzzle.kafka.order.entity.ShopOrderDTO;
import ch.puzzle.kafka.stock.entity.ArticleStockRequest;
import ch.puzzle.kafka.util.HeadersMapExtractAdapter;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ArticleStockRequestProducer {

    @Inject
    @Channel("article-stock-request")
    Emitter<ArticleStockRequest> emitter;

    @Inject
    Tracer tracer;

    @Traced
    public void createRequest(ShopOrderDTO shopOrderDTO) {
        ArticleStockRequest articleStockRequest = new ArticleStockRequest(shopOrderDTO);
        HeadersMapExtractAdapter headersMapExtractAdapter = new HeadersMapExtractAdapter();
        try (Scope scope = tracer.buildSpan("sendMessage").startActive(true)) {
            tracer.inject(scope.span().context(), Format.Builtin.TEXT_MAP, headersMapExtractAdapter);
            OutgoingKafkaRecordMetadata metadata = OutgoingKafkaRecordMetadata.<ArticleStockRequest>builder()
                    .withKey(articleStockRequest)
                    .withTopic("manual")
                    .withHeaders(headersMapExtractAdapter.getRecordHeaders())
                    .build();
            Message<ArticleStockRequest> message = Message.of(articleStockRequest, Metadata.of(metadata));
            emitter.send(message);
        }
    }
}
