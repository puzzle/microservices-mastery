FROM quay.io/quarkus/centos-quarkus-maven:20.3.2-java11 AS build
COPY pom.xml /usr/src/app/
RUN mvn -f /usr/src/app/pom.xml -B de.qaware.maven:go-offline-maven-plugin:1.2.5:resolve-dependencies
COPY src /usr/src/app/src
USER root
RUN chown -R quarkus /usr/src/app
USER quarkus
RUN mvn -f /usr/src/app/pom.xml clean package

FROM strimzi/kafka:0.20.1-kafka-2.6.0 AS connect
ENV KAFKA_CONNECT_PLUGIN_PATH=/tmp/connect-plugins
ENV DEBEZIUM_DIR=$KAFKA_CONNECT_PLUGIN_PATH/debezium-connect

USER root
RUN mkdir -p /opt/kafka/logs \
    && mkdir -p /opt/kafka/custom-config/ \
    && chown -R kafka:root /opt/kafka
USER kafka

COPY src/main/docker/config/* /opt/kafka/custom-config/

RUN mkdir -p $DEBEZIUM_DIR \
    && cd $KAFKA_CONNECT_PLUGIN_PATH \
    && curl -sfSL https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/1.4.1.Final/debezium-connector-postgres-1.4.1.Final-plugin.tar.gz | tar xz

COPY --from=build /usr/src/app/target/event-routers-*.jar $DEBEZIUM_DIR
