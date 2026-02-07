package fun.ninth.quorum.cluster;

import java.net.URI;

import fun.ninth.quorum.node.NodeId;

public class Peer {
    private NodeId nodeId;
    private int port;
    private URI endpoint;

    @SuppressWarnings("unused")
    public Peer() {
    }

    public Peer(NodeId nodeId, int port, URI endpoint) {
        this.nodeId = nodeId;
        this.port = port;
        this.endpoint = endpoint;
    }

    @SuppressWarnings("unused")
    public NodeId getNodeId() {
        return nodeId;
    }

    @SuppressWarnings("unused")
    public void setNodeId(NodeId nodeId) {
        this.nodeId = nodeId;
    }

    @SuppressWarnings("unused")
    public URI getEndpoint() {
        return endpoint;
    }

    @SuppressWarnings("unused")
    public void setEndpoint(URI endpoint) {
        this.endpoint = endpoint;
    }

    @SuppressWarnings("unused")
    public int getPort() {
        return port;
    }

    @SuppressWarnings("unused")
    public void setPort(int port) {
        this.port = port;
    }
}
