package ch.puzzle.mm.kafka.order.order.boundary;

import ch.puzzle.mm.kafka.order.order.control.ShopOrderService;
import ch.puzzle.mm.kafka.order.order.entity.ShopOrderDTO;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.concurrent.CompletableFuture;
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
    public CompletionStage<Void> consumeOrders(KafkaRecord<String, ShopOrderDTO> message) {
        return CompletableFuture.runAsync(() -> {
            try (Scope scope = tracer.buildSpan("compensate-order").asChildOf(TracingKafkaUtils.extractSpanContext(message.getHeaders(), tracer)).startActive(true)) {
                compensateOrder(message);
            }
        }).thenRun(message::ack);
    }

    private void compensateOrder(KafkaRecord<String, ShopOrderDTO> message) {
        shopOrderService.compensateOrder(message.getPayload().id);
    }
}

