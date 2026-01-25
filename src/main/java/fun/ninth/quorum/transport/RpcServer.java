package fun.ninth.quorum.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import fun.ninth.quorum.transport.requests.RpcMessageType;

public class RpcServer {
    private final HttpServer server;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<RpcMessageType, Consumer<RpcEnvelope>> handlers = new ConcurrentHashMap<>();

    public RpcServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/rpc", this::handle);
        server.setExecutor(Executors.newFixedThreadPool(4));
    }

    public void start() {
        server.start();
        System.out.println("Server started listening on port: " + server.getAddress());
    }

    public void registerHandler(RpcMessageType messageType, Consumer<RpcEnvelope> handler) {
        handlers.put(messageType, handler);
    }

    private void handle(HttpExchange httpExchange) throws IOException {
        RpcEnvelope envelope = mapper.readValue(httpExchange.getRequestBody(), RpcEnvelope.class);
        Consumer<RpcEnvelope> handler = handlers.getOrDefault(envelope.getMessageType(), null);
        if (handler == null) {
            httpExchange.sendResponseHeaders(404, -1);
            return;
        }
        handler.accept(envelope);
        httpExchange.sendResponseHeaders(202, -1);
    }
}
