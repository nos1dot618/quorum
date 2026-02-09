package fun.ninth.quorum.node;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.raft.RaftEnvelope;
import fun.ninth.quorum.raft.RaftNode;
import fun.ninth.quorum.raft.RaftPeer;
import fun.ninth.quorum.raft.messages.IRaftMessage;
import fun.ninth.quorum.raft.transport.IRaftTransport;
import fun.ninth.quorum.transport.RpcClient;
import fun.ninth.quorum.transport.RpcServer;

public class Node {
    private final NodeId nodeId;
    /// The peer that the server is listening at.
    private final Peer serverPeer;
    private final RpcServer<RaftEnvelope> server;
    private final RpcClient client;
    private final RaftNode raftNode;

    class RaftTransport implements IRaftTransport {
        @Override
        public void send(Peer ignore, Peer peer, IRaftMessage message) {
            // TODO: Replace hard-coded replication-group Id.
            // TODO: Fix this design sourcePeer only makes sense for testing.
            RaftEnvelope envelope =
                    new RaftEnvelope(serverPeer, peer, UUID.randomUUID().toString(), "Shard-A", message);
            client.send(peer, envelope);
        }

        // TODO: This is a poor design as list of peers falls into cluster domain.
        @Override
        public List<Peer> getPeers() {
            return List.of();
        }
    }

    public Node(NodeId nodeId, int port) throws IOException {
        this.nodeId = nodeId;
        this.serverPeer = new RaftPeer(nodeId, port);
        IRaftTransport raftTransport = new RaftTransport();
        this.raftNode = new RaftNode(serverPeer, raftTransport);
        this.server = new RpcServer<>(port, RaftEnvelope.class, raftNode::rpcHandler);
        this.client = new RpcClient();
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public RpcServer<RaftEnvelope> getServer() {
        return server;
    }

    public RpcClient getClient() {
        return client;
    }

    public RaftNode getRaftNode() {
        return raftNode;
    }

    public void start() {
        server.start();
        raftNode.start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void stop() {
        raftNode.stop();
        server.stop();
    }
}
