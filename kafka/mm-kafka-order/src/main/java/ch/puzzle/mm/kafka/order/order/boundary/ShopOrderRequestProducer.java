package ch.puzzle.mm.kafka.order.order.boundary;

import ch.puzzle.mm.kafka.order.order.entity.ShopOrderDTO;
import io.opentracing.Tracer;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ShopOrderRequestProducer {

    private final Logger log = LoggerFactory.getLogger(ShopOrderRequestProducer.class.getName());

    @Inject
    @Channel("shop-order-request")
    Emitter<ShopOrderDTO> emitter;

    @Inject
    Tracer tracer;

    @Traced
    public void createRequest(ShopOrderDTO shopOrderDTO) {
        emitter.send(shopOrderDTO);
    }
}
