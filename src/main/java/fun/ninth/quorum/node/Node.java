package fun.ninth.quorum.node;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.raft.IRaftTransport;
import fun.ninth.quorum.raft.RaftEnvelope;
import fun.ninth.quorum.raft.RaftNode;
import fun.ninth.quorum.raft.RaftPeer;
import fun.ninth.quorum.raft.messages.AppendEntriesRequest;
import fun.ninth.quorum.raft.messages.IRaftMessage;
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
        public void send(Peer peer, IRaftMessage message) {
            // TODO: Replace hard-coded replication-group Id.
            RaftEnvelope envelope = new RaftEnvelope(serverPeer, peer, UUID.randomUUID().toString(), "Shard-A", message);
            client.send(peer, envelope);
        }
    }

    public Node(NodeId nodeId, int port) throws IOException {
        this.nodeId = nodeId;
        IRaftTransport raftTransport = new RaftTransport();
        this.raftNode = new RaftNode(nodeId, raftTransport);
        this.server = new RpcServer<>(port, RaftEnvelope.class, raftNode::rpcHandler);
        this.client = new RpcClient();
        this.serverPeer = new RaftPeer(nodeId, port);
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
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

    public void sendAppendEntriesRequest(Peer peer) {
        AppendEntriesRequest request = new AppendEntriesRequest(2, 10, 1, List.of("put a=1", "put b=2"), 12);
        RaftEnvelope envelope = new RaftEnvelope(serverPeer, peer, UUID.randomUUID().toString(), "Shard-A", request);
        client.send(peer, envelope);
    }
}
