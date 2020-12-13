package ch.puzzle.mm.debezium.connect.smt;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ChangeTopicCase<R extends ConnectRecord<R>> implements Transformation<R> {

    public static final String CASE_CONFIG = "toCase";

    public static Logger logger = LoggerFactory.getLogger(ChangeTopicCase.class.getName());

    public ChangeTopicCase() { }

    boolean toLowercase = true;

    @Override
    public R apply(R record) {
        final String newTopic = toLowercase ? record.topic().toLowerCase() : record.topic().toUpperCase();

        return record.newRecord(
                newTopic,
                record.kafkaPartition(),
                record.keySchema(),
                record.key(),
                record.valueSchema(),
                record.value(),
                record.timestamp()
        );
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef()
                .define(CASE_CONFIG, ConfigDef.Type.STRING, "lower", null,
                        ConfigDef.Importance.HIGH, "Case (UPPER/LOWER)");
    }

    @Override
    public void configure(Map<String, ?> settings) {
        if(settings.containsKey(CASE_CONFIG)) {
            toLowercase = settings.get(CASE_CONFIG).toString().toLowerCase().contains("low");
        }
    }

    @Override
    public void close() {

    }
}
