package fun.ninth.quorum.cluster;

import java.net.URI;

import fun.ninth.quorum.node.NodeId;

public class Peer {
    private final NodeId nodeId;
    private final URI endpoint;

    public Peer(NodeId nodeId, URI endpoint) {
        this.nodeId = nodeId;
        this.endpoint = endpoint;
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public URI getEndpoint() {
        return endpoint;
    }
}
