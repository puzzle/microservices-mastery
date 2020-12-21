package ch.puzzle.mm.kafka.viewer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.mvel2.util.Make;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class DataConsumer {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Incoming("kafkamessages")
    @Outgoing("websocket-broadcast")
    public String streamHtmlMessages(Message<String> msg) {
        msg.ack();
        return getKafkaMetadata(msg) + " " + wrapSpan(msg.getPayload(), "payload", "")+" "+getKafkaHeaderString(msg);
    }

    String getKafkaMetadata(Message<String> msg) {
        Optional<IncomingKafkaRecordMetadata> meta = msg.getMetadata(IncomingKafkaRecordMetadata.class);
        String topic = meta.map(IncomingKafkaRecordMetadata::getTopic).orElse("unknown");
        return wrapSpan(topic, "topic", topic);
    }

    String getKafkaHeaderString(Message<String> msg) {
        Optional<IncomingKafkaRecordMetadata> meta = msg.getMetadata(IncomingKafkaRecordMetadata.class);

        StringBuffer sb = new StringBuffer();
        for (Header h : meta.get().getHeaders()) {
            sb.append("<span>").append(h.key()).append(" -> ").append(new String(h.value())).append("</span><br />");
        }

        return sb.toString();
    }

    String wrapSpan(String msg, String css1, String css2) {
        return String.format("<span class='%s %s'>%s</span>", css1, css2, msg);
    }
}
