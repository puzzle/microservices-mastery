package ch.puzzle.mm.kafka.viewer;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint("/stream/{clientId}")
public class DataSocket {

    private static final Logger logger = LoggerFactory.getLogger(DataSocket.class.getName());
    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("clientId") String clientId) {
        logger.info("Client " + clientId + " connected");
        sessions.put(UUID.randomUUID().toString(), session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("clientId") String clientId) {
        logger.info("Client " + clientId + " disconnected");
        sessions.remove(clientId);
    }

    @OnError
    public void onError(Session session, @PathParam("clientId") String clientId, Throwable throwable) {
        logger.info("Client " + clientId + " is erroneous");
        sessions.remove(clientId);
    }

    @Incoming("websocket-broadcast")
    public void broadcast(String message) {
        logger.info("Broadcasting to {} client(s): {}", sessions.size(), message);
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    logger.info("Unable to send message: " + result.getException());
                }
            });
        });
    }
}
