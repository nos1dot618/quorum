package fun.ninth.quorum.raft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.raft.messages.IRaftMessage;

/// IRaftTransport implementation without RPC, useful for testing and debugging.
public class InMemoryRaftTransport implements IRaftTransport {
    private final Map<Peer, RaftNode> nodeMap = new ConcurrentHashMap<>();
    private final List<Peer> peers = new ArrayList<>();
    private final List<RaftEnvelope> sentEnvelopes = new ArrayList<>();

    public void register(Peer peer, RaftNode raftNode) {
        nodeMap.put(peer, raftNode);
        peers.add(peer);
    }

    @Override
    public void send(Peer peer, IRaftMessage message) {
        RaftNode targetNode = nodeMap.get(peer);
        if (targetNode == null) throw new IllegalStateException(String.format("Unknown peer %s", peer));
        // sourcePeer is not needed for test
        RaftEnvelope envelope = new RaftEnvelope(null, peer, UUID.randomUUID().toString(),
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
}
