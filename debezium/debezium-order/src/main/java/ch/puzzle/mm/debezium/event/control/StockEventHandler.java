package ch.puzzle.mm.debezium.event.control;

import ch.puzzle.mm.debezium.order.control.ShopOrderService;
import ch.puzzle.mm.debezium.order.entity.ShopOrderStockResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class StockEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(StockEventHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    EventLog eventLog;

    @Inject
    ShopOrderService shopOrderService;

    @Transactional
    public void onStockEvent(UUID eventId, String eventType, String key, String event, Instant ts) {
        if (eventLog.alreadyProcessed(eventId)) {
            logger.info("Event with id {} was already processed, ignore.", eventId);
            return;
        }

        logger.info("Received '{}' event {} - OrderId: {}, ts: '{}'", eventType, eventId, key, ts);
        if (eventType.equalsIgnoreCase("StockComplete")) {
            shopOrderService.onStockCompleteEvent(deserialize(event));
        } else if (eventType.equalsIgnoreCase("StockIncomplete")) {
            shopOrderService.onStockIncompleteEvent(deserialize(event));
        } else {
            logger.warn("Ignoring unknown event '{}'", eventType);
        }

        eventLog.processed(eventId);
    }

    ShopOrderStockResponse deserialize(String escapedEvent) {
        try {
            String unescaped = objectMapper.readValue(escapedEvent, String.class);
            return objectMapper.readValue(unescaped, ShopOrderStockResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize message. " + escapedEvent, e);
        }
    }
}
