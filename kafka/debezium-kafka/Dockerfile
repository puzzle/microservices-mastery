FROM quay.io/debezium/kafka:1.5

USER root
COPY jmx_prometheus_javaagent-0.14.0.jar $KAFKA_HOME/jmx_prometheus_javaagent-0.14.0.jar
COPY jmx_exporter.yml $KAFKA_HOME/jmx_exporter.yml

ENV KAFKA_OPTS="-javaagent:$KAFKA_HOME/jmx_prometheus_javaagent-0.14.0.jar=8080:/kafka/jmx_exporter.yml"

RUN chown -R kafka $KAFKA_HOME && \
    chgrp -R kafka $KAFKA_HOME && \
    chmod -R g+w,o+w $KAFKA_HOME

USER kafka