package fun.ninth.quorum.raft;

import java.net.URI;
import java.net.URISyntaxException;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.node.NodeId;

public class RaftPeer extends Peer {
    public RaftPeer(NodeId nodeId, int port) {
        super(nodeId, port, buildUri(port));
    }

    public static URI buildUri(int port) {
        try {
            return new URI("http", null, "localhost", port, "/rpc", null, null);
        } catch (URISyntaxException e) {
            throw new AssertionError("Impossible URI syntax error", e);
        }
    }
}
