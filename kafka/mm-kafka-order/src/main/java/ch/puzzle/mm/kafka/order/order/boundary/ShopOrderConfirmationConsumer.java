package ch.puzzle.mm.kafka.order.order.boundary;

import ch.puzzle.mm.kafka.order.order.control.ShopOrderService;
import ch.puzzle.mm.kafka.order.order.entity.ShopOrderDTO;
import ch.puzzle.mm.kafka.order.util.HeadersMapExtractAdapter;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import io.opentracing.propagation.Format;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@Traced
public class ShopOrderConfirmationConsumer {

    private final Logger logger = LoggerFactory.getLogger(ShopOrderConfirmationConsumer.class.getName());

    @Inject
    ShopOrderService shopOrderService;

    @Inject
    Tracer tracer;

    @Incoming("shop-order-confirmation")
    public CompletionStage<Void> consumeOrders(KafkaRecord<String, ShopOrderDTO> message) {
        return CompletableFuture.runAsync(() -> {
            try (Scope scope = tracer.buildSpan("confirm-order").asChildOf(TracingKafkaUtils.extractSpanContext(message.getHeaders(), tracer)).startActive(true)) {
                confirmOrder(message);
            }
        }).thenRun(message::ack);
    }

    private void confirmOrder(KafkaRecord<String, ShopOrderDTO> message) {
        shopOrderService.confirmOrder(message.getPayload().id);
    }
}

