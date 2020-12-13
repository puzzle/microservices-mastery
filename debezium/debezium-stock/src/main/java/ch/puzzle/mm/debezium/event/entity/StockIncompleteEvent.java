package ch.puzzle.mm.debezium.event.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.debezium.outbox.quarkus.ExportedEvent;

import java.time.Instant;
import java.util.UUID;

public class StockIncompleteEvent implements ExportedEvent<String, JsonNode> {

    private static ObjectMapper mapper = new ObjectMapper();

    private static final String TYPE = "ArticleStock";
    private static final String EVENT_TYPE = "StockIncomplete";

    private final UUID id;
    private final Long orderId;
    private final JsonNode jsonNode;
    private final Instant timestamp;

    public StockIncompleteEvent(Instant created, Long orderId) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.timestamp = created;
        this.jsonNode = asJson(orderId);
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(orderId);
    }

    @Override
    public String getAggregateType() {
        return TYPE;
    }

    @Override
    public String getType() {
        return EVENT_TYPE;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public JsonNode getPayload() {
        return jsonNode;
    }

    public ObjectNode asJson(Long orderId) {
        return mapper.createObjectNode()
                .put("orderId", orderId);
    }
}
