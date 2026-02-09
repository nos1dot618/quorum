package fun.ninth.quorum.cluster;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fun.ninth.quorum.node.NodeId;

public class Peer {
    private final NodeId nodeId;
    private final int port;
    private final URI endpoint;

    @JsonCreator
    public Peer(@JsonProperty("nodeId") NodeId nodeId, @JsonProperty("port") int port,
                @JsonProperty("endpoint") URI endpoint)
    {
        this.nodeId = nodeId;
        this.port = port;
        this.endpoint = endpoint;
    }

    @SuppressWarnings("unused")
    public NodeId getNodeId() {
        return nodeId;
    }

    @SuppressWarnings("unused")
    public URI getEndpoint() {
        return endpoint;
    }

    @SuppressWarnings("unused")
    public int getPort() {
        return port;
    }

    public boolean equals(Object object) {
        if (object == null) return false;
        if (object instanceof Peer other) {
            return nodeId.equals(other.nodeId) && port == other.port;
        }
        return false;
    }
}
