package fun.ninth.quorum.raft;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.node.NodeId;
import fun.ninth.quorum.raft.messages.AppendEntriesRequest;
import fun.ninth.quorum.raft.messages.AppendEntriesResponse;
import fun.ninth.quorum.utils.DirectExecutorService;

public class RaftNodeAppendEntriesTest {
    @Test
    void followerRejectsAppendEntriesWithOlderEpoch() {
        InMemoryRaftTransport transport = new InMemoryRaftTransport();
        DirectExecutorService executorService = new DirectExecutorService();
        RaftNode peer1RaftNode = new RaftNode(transport, executorService);
        Peer peer1 = new RaftPeer(new NodeId(), 9000); // Epoch 1 leader.
        transport.register(peer1, peer1RaftNode);
        // Advance Epoch.
        {
            long expectedEpoch = 5;
            AppendEntriesRequest request = new AppendEntriesRequest(expectedEpoch, -1, -1, null, -1);
            peer1RaftNode.rpcHandler(new RaftEnvelope(peer1, null, "1", "Test-Shard", request));
            List<RaftEnvelope> envelopes = transport.drainEnvelopes();

            Assertions.assertEquals(1, envelopes.size());
            Assertions.assertInstanceOf(AppendEntriesResponse.class, envelopes.getFirst().getMessage());
            AppendEntriesResponse response = (AppendEntriesResponse) envelopes.getFirst().getMessage();
            Assertions.assertEquals(expectedEpoch, response.getEpoch());
            Assertions.assertTrue(response.isSuccess());
        }
        // Send stale log entries.
        {
            long expectedEpoch = 5;
            AppendEntriesRequest request = new AppendEntriesRequest(3, -1, -1, null, -1);
            peer1RaftNode.rpcHandler(new RaftEnvelope(peer1, null, "1", "Test-Shard", request));
            List<RaftEnvelope> envelopes = transport.drainEnvelopes();

            Assertions.assertEquals(1, envelopes.size());
            Assertions.assertInstanceOf(AppendEntriesResponse.class, envelopes.getFirst().getMessage());
            AppendEntriesResponse response = (AppendEntriesResponse) envelopes.getFirst().getMessage();
            Assertions.assertEquals(expectedEpoch, response.getEpoch());
            Assertions.assertFalse(response.isSuccess());
        }
    }
}
