package ch.puzzle.mm.kafka.order.order.boundary;

import ch.puzzle.mm.kafka.order.order.control.ShopOrderService;
import ch.puzzle.mm.kafka.order.order.entity.ShopOrderDTO;
import ch.puzzle.mm.kafka.order.util.HeadersMapExtractAdapter;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.smallrye.context.SmallRyeManagedExecutor;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

    @Inject
    SmallRyeManagedExecutor executor;

    @Incoming("shop-order-confirmation")
    public CompletionStage<Void> consumeOrders(Message<ShopOrderDTO> message) {
        Optional<IncomingKafkaRecordMetadata> metadata = message.getMetadata(IncomingKafkaRecordMetadata.class);
        if (metadata.isPresent()) {
            SpanContext extract = tracer.extract(Format.Builtin.TEXT_MAP, new HeadersMapExtractAdapter(metadata.get().getHeaders()));
            try (Scope scope = tracer.buildSpan("consume-confirmation").asChildOf(extract).startActive(true)) {
                confirmOrder(message.getPayload());
                return message.ack();
            }
        }
        return message.nack(new RuntimeException());
    }

    private void confirmOrder(ShopOrderDTO shopOrderDTO) {
        executor.runAsync(() -> shopOrderService.confirmOrder(shopOrderDTO.id));
    }
}

