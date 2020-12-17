package ch.puzzle.mm.kafka.order.order.boundary;

import ch.puzzle.mm.kafka.order.order.control.ShopOrderService;
import ch.puzzle.mm.kafka.order.order.entity.ShopOrderDTO;
import ch.puzzle.mm.kafka.order.util.HeadersMapExtractAdapter;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@Traced
public class ShopOrderCompensationConsumer {

    private final Logger logger = LoggerFactory.getLogger(ShopOrderCompensationConsumer.class.getName());

    @Inject
    ShopOrderService shopOrderService;

    @Inject
    Tracer tracer;

    @Incoming("shop-order-compensation")
    public CompletionStage<Void> consumeOrders(Message<ShopOrderDTO> message) {
        Optional<IncomingKafkaRecordMetadata> metadata = message.getMetadata(IncomingKafkaRecordMetadata.class);
        return compensateOrder(message, metadata);
    }

    @Transactional
    private CompletionStage<Void> compensateOrder(Message<ShopOrderDTO> message, Optional<IncomingKafkaRecordMetadata> metadata) {
        SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new HeadersMapExtractAdapter(metadata.get().getHeaders()));
        try (Scope scope = tracer.buildSpan("compensate-order").asChildOf(spanContext).startActive(true)) {
            ManagedExecutor executor = ManagedExecutor.builder()
                    .maxAsync(5)
                    .propagated(ThreadContext.CDI, ThreadContext.TRANSACTION)
                    .build();
            executor.runAsync(() -> shopOrderService.compensateOrder(message.getPayload().id));
            return message.ack();
        }
    }
}
