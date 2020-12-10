package ch.puzzle.mm.kafka.stock.boundary;

import ch.puzzle.mm.kafka.order.entity.ShopOrderDTO;
import ch.puzzle.mm.kafka.util.HeadersMapExtractAdapter;
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
    Emitter<ShopOrderDTO> emitter;

    @Inject
    Tracer tracer;

    @Traced
    public void createRequest(ShopOrderDTO shopOrderDTO) {
        HeadersMapExtractAdapter headersMapExtractAdapter = new HeadersMapExtractAdapter();
        try (Scope scope = tracer.buildSpan("sendMessage").startActive(true)) {
            tracer.inject(scope.span().context(), Format.Builtin.TEXT_MAP, headersMapExtractAdapter);
            OutgoingKafkaRecordMetadata metadata = OutgoingKafkaRecordMetadata.<ShopOrderDTO>builder()
                    .withKey(shopOrderDTO)
                    .withTopic("manual")
                    .withHeaders(headersMapExtractAdapter.getRecordHeaders())
                    .build();
            Message<ShopOrderDTO> message = Message.of(shopOrderDTO, Metadata.of(metadata));
            emitter.send(message);
        }
    }
}
