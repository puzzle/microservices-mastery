package ch.puzzle.mm.debezium.viewer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class DataConsumer {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Incoming("kafkamessages")
    @Outgoing("websocket-broadcast")
    public String streamHtmlMessages(Message<String> msg) {
        msg.ack();
        try {
            String unescaped = objectMapper.readValue(msg.getPayload(), String.class);
            return getKafkaMetadata(msg) + " " + wrapSpan(unescaped, "payload", "");
        } catch (JsonProcessingException e) {
            return "unknown message";
        }
    }

    String getKafkaMetadata(Message<String> msg) {
        Optional<IncomingKafkaRecordMetadata> meta = msg.getMetadata(IncomingKafkaRecordMetadata.class);
        String topic = meta.map(IncomingKafkaRecordMetadata::getTopic).orElse("unknown");
        return wrapSpan(topic, "topic", topic);
    }

    String wrapSpan(String msg, String css1, String css2) {
        return String.format("<span class='%s %s'>%s</span>", css1, css2, msg);
    }
}
