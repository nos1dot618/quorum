package fun.ninth.quorum.node;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.raft.RaftNode;
import fun.ninth.quorum.transport.RpcClient;
import fun.ninth.quorum.transport.RpcEnvelope;
import fun.ninth.quorum.transport.RpcServer;
import fun.ninth.quorum.transport.requests.AppendEntriesRequest;
import fun.ninth.quorum.transport.requests.RpcMessageType;

public class Node {
    private final NodeId nodeId;
    private final RpcServer server;
    private final RpcClient client;
    private final RaftNode raftNode;

    public Node(NodeId nodeId, int port) throws IOException {
        this.nodeId = nodeId;
        this.server = new RpcServer(port);
        this.client = new RpcClient();
        this.raftNode = new RaftNode();
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public RpcServer getServer() {
        return server;
    }

    public RpcClient getClient() {
        return client;
    }

    public RaftNode getRaftNode() {
        return raftNode;
    }

    public void start() {
        server.registerHandler(RpcMessageType.APPEND_ENTRIES_REQUEST, raftNode::rpcHandler);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

    public void sendAppendEntriesRequest(Peer peer) {
        AppendEntriesRequest request = new AppendEntriesRequest(10, 1, List.of("put a=1", "put b=2"), 12);
        ObjectMapper mapper = new ObjectMapper();
        RpcEnvelope envelope = new RpcEnvelope(
                nodeId,
                peer.getNodeId(),
                RpcMessageType.APPEND_ENTRIES_REQUEST,
                UUID.randomUUID().toString(),
                "Shard-A",
                2,
                mapper.valueToTree(request)
        );
        client.send(peer, envelope);
    }
}
