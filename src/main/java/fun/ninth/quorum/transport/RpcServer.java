package fun.ninth.quorum.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class RpcServer<T extends RpcEnvelope> {
    private final HttpServer server;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Consumer<T> handler;
    private final Class<T> envelopeType;

    public RpcServer(int port, Class<T> envelopeType, Consumer<T> handler) throws IOException {
        this.envelopeType = envelopeType;
        this.handler = handler;

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/rpc", this::handle);
        server.setExecutor(Executors.newFixedThreadPool(4));
    }

    public void start() {
        server.start();
        System.out.println("RPC-Server started listening at port: " + server.getAddress());
    }

    public void stop() {
        server.stop(0);
        System.out.println("RPC-Server stopped listening at port: " + server.getAddress());
    }

    private void handle(HttpExchange httpExchange) throws IOException {
        T envelope = mapper.readValue(httpExchange.getRequestBody(), envelopeType);
        handler.accept(envelope);
        httpExchange.sendResponseHeaders(200, -1);
    }
}
