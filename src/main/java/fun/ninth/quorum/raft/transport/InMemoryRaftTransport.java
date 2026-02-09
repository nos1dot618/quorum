package fun.ninth.quorum.raft.transport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.raft.RaftEnvelope;
import fun.ninth.quorum.raft.RaftNode;
import fun.ninth.quorum.raft.messages.IRaftMessage;

/// IRaftTransport implementation without RPC, useful for testing and debugging.
public class InMemoryRaftTransport implements IRaftTransport {
    private final Map<Peer, RaftNode> raftNodeMap = new ConcurrentHashMap<>();
    private final List<Peer> peers = new ArrayList<>();
    private final List<RaftEnvelope> sentEnvelopes = new ArrayList<>();

    public void register(Peer peer, RaftNode raftNode) {
        raftNodeMap.put(peer, raftNode);
        peers.add(peer);
    }

    @Override
    public void send(Peer sourcePeer, Peer destinationPeer, IRaftMessage message) {
        RaftNode targetNode = raftNodeMap.get(destinationPeer);
        if (targetNode == null) throw new IllegalStateException(String.format("Unknown peer %s", destinationPeer));
        // sourcePeer is not needed for test
        RaftEnvelope envelope = new RaftEnvelope(sourcePeer, destinationPeer, UUID.randomUUID().toString(),
                "Test-Shard", message);
        sentEnvelopes.add(envelope);
        targetNode.rpcHandler(envelope);
    }

    @Override
    public List<Peer> getPeers() {
        return List.copyOf(peers);
    }

    public List<RaftEnvelope> drainEnvelopes() {
        List<RaftEnvelope> envelopes = List.copyOf(sentEnvelopes);
        sentEnvelopes.clear();
        return envelopes;
    }

    public void forEachRaftNode(Consumer<RaftNode> operation) {
        raftNodeMap.values().forEach(operation);
    }
}
