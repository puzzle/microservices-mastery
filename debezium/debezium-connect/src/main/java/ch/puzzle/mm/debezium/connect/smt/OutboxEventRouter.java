package ch.puzzle.mm.debezium.connect.smt;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.header.Headers;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OutboxEventRouter<R extends ConnectRecord<R>> implements Transformation<R> {

    public static Logger logger = LoggerFactory.getLogger(OutboxEventRouter.class.getName());

    public OutboxEventRouter() { }

    @Override
    public R apply(R record) {
        // Ignoring tombstones just in case
        if (record.value() == null) {
            return record;
        }

        Struct struct = (Struct) record.value();
        String op = struct.getString("op");

        // ignoring deletions in the outbox table
        if (op.equals("c")) {
            Long timestamp = struct.getInt64("ts_ms");
            Struct after = struct.getStruct("after");

            String key = after.getString("aggregateid");
            String topic = after.getString("aggregatetype").toLowerCase() + "-" + after.getString("type").toLowerCase() + "-events";

            String eventId = after.getString("id");
            String eventType = after.getString("type");
            String tracingSpanContext = after.getString("tracingspancontext");
            String payload = after.getString("payload");

            Schema valueSchema = SchemaBuilder.struct()
                    .field("eventType", after.schema().field("type").schema())
                    .field("ts_ms", struct.schema().field("ts_ms").schema())
                    .field("payload", after.schema().field("payload").schema())
                    .build();

            Struct value = new Struct(valueSchema)
                    .put("eventType", eventType)
                    .put("ts_ms", timestamp)
                    .put("payload", payload);

            Headers headers = record.headers();
            headers.addString("eventId", eventId);
            headers.addString("eventType", eventType);
            headers.addString("tracingspancontext", tracingSpanContext);

            return record.newRecord(topic, null, Schema.STRING_SCHEMA, key, valueSchema, value, record.timestamp(), headers);
        } else {
            // only care for the create event
            return null;
        }
    }

    @Override
    public void configure(Map<String, ?> map) { }

    @Override
    public ConfigDef config() {
        return new ConfigDef();
    }

    @Override
    public void close() { }
}
