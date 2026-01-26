package fun.ninth.quorum.transport;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;

import fun.ninth.quorum.cluster.Peer;

public class RpcClient {
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public RpcClient() {
        client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(200)).build();
    }

    public void send(Peer peer, RpcEnvelope envelope) {
        try {
            String serializedObject = mapper.writeValueAsString(envelope);
            HttpRequest request = HttpRequest.newBuilder(peer.getEndpoint())
                    .POST(HttpRequest.BodyPublishers.ofString(serializedObject))
                    .timeout(Duration.ofMillis(500))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            System.out.println("RPC-Client failed to send envelope: " + e);
        }
    }

}
