package fun.ninth.quorum.raft;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.node.NodeId;
import fun.ninth.quorum.raft.logs.Ledger;
import fun.ninth.quorum.raft.logs.LogEntry;
import fun.ninth.quorum.raft.messages.AppendEntriesRequest;
import fun.ninth.quorum.raft.messages.AppendEntriesResponse;
import fun.ninth.quorum.raft.messages.RequestVoteRequest;
import fun.ninth.quorum.raft.messages.RequestVoteResponse;
import fun.ninth.quorum.raft.transport.InMemoryRaftTransport;
import fun.ninth.quorum.storage.raft.InMemoryRaftLogStore;
import fun.ninth.quorum.storage.raft.InMemoryRaftMetadataStore;
import fun.ninth.quorum.utils.DirectExecutorService;

public class RaftNodeTest {
    private InMemoryRaftTransport transport;
    private RaftNode peer1RaftNode;
    private Peer peer1, peer2, peer3;

    @BeforeEach
    void setup() {
        transport = new InMemoryRaftTransport();
        DirectExecutorService executorService = new DirectExecutorService();
        peer1 = new RaftPeer(new NodeId("1"), 9001);
        peer2 = new RaftPeer(new NodeId("2"), 9002);
        peer3 = new RaftPeer(new NodeId("3"), 9003);
        peer1RaftNode = new RaftNode(peer1, transport, executorService, null, new InMemoryRaftMetadataStore(),
                new InMemoryRaftLogStore());
        RaftNode peer2RaftNode = new RaftNode(peer2, transport, executorService, null, new InMemoryRaftMetadataStore(),
                new InMemoryRaftLogStore());
        RaftNode peer3RaftNode = new RaftNode(peer3, transport, executorService, null, new InMemoryRaftMetadataStore(),
                new InMemoryRaftLogStore());
        transport.register(peer1, peer1RaftNode);
        transport.register(peer2, peer2RaftNode);
        transport.register(peer3, peer3RaftNode);
        transport.forEachRaftNode(RaftNode::start);
    }

    @AfterEach
    void teardown() {
        transport.forEachRaftNode(RaftNode::stop);
    }

    @Test
    void followerRejectsAppendEntriesWithOlderEpoch() {
        // Advance Epoch.
        {
            long expectedEpoch = 5;
            AppendEntriesRequest request = new AppendEntriesRequest(expectedEpoch, -1, -1, null, -1);
            peer1RaftNode.rpcHandler(new RaftEnvelope(peer2, peer1, "1", "Test-Shard", request));
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
            peer1RaftNode.rpcHandler(new RaftEnvelope(peer2, peer1, "1", "Test-Shard", request));
            List<RaftEnvelope> envelopes = transport.drainEnvelopes();

            Assertions.assertEquals(1, envelopes.size());
            Assertions.assertInstanceOf(AppendEntriesResponse.class, envelopes.getFirst().getMessage());
            AppendEntriesResponse response = (AppendEntriesResponse) envelopes.getFirst().getMessage();
            Assertions.assertEquals(expectedEpoch, response.getEpoch());
            Assertions.assertFalse(response.isSuccess());
        }
    }

    @Test
    void followerGrantsOnlyOneVotePerEpoch() {
        {
            long expectedEpoch = 1;
            RequestVoteRequest request = new RequestVoteRequest(expectedEpoch, -1, -1);
            peer1RaftNode.rpcHandler(new RaftEnvelope(peer2, peer1, "1", "Test-Shard", request));
            List<RaftEnvelope> envelopes = transport.drainEnvelopes();

            Assertions.assertEquals(1, envelopes.size());
            Assertions.assertInstanceOf(RequestVoteResponse.class, envelopes.getFirst().getMessage());
            RequestVoteResponse response = (RequestVoteResponse) envelopes.getFirst().getMessage();
            Assertions.assertEquals(expectedEpoch, response.getEpoch());
            Assertions.assertTrue(response.isVoteGranted());
        }

        // Vote will not be granted as RaftNode 1 already granted vote to Peer 2.
        {
            long expectedEpoch = 1;
            RequestVoteRequest request = new RequestVoteRequest(expectedEpoch, -1, -1);
            peer1RaftNode.rpcHandler(new RaftEnvelope(peer3, peer1, "2", "Test-Shard", request));
            List<RaftEnvelope> envelopes = transport.drainEnvelopes();

            Assertions.assertEquals(1, envelopes.size());
            Assertions.assertInstanceOf(RequestVoteResponse.class, envelopes.getFirst().getMessage());
            RequestVoteResponse response = (RequestVoteResponse) envelopes.getFirst().getMessage();
            Assertions.assertEquals(expectedEpoch, response.getEpoch());
            Assertions.assertFalse(response.isVoteGranted());
        }
    }

    @Test
    void voteRejectedWhenCandidateLogIsBehind() {
        Ledger ledger = new Ledger(List.of(new LogEntry(1, "cmd")));

        // Leader first appends an entry into the follower's log.
        {
            long expectedEpoch = 1;
            AppendEntriesRequest request = new AppendEntriesRequest(expectedEpoch, -1, -1, ledger, -1);
            peer1RaftNode.rpcHandler(new RaftEnvelope(peer2, peer1, "1", "Test-Shard", request));
            List<RaftEnvelope> envelopes = transport.drainEnvelopes();

            Assertions.assertEquals(1, envelopes.size());
            Assertions.assertInstanceOf(AppendEntriesResponse.class, envelopes.getFirst().getMessage());
            AppendEntriesResponse response = (AppendEntriesResponse) envelopes.getFirst().getMessage();
            Assertions.assertEquals(expectedEpoch, response.getEpoch());
            Assertions.assertTrue(response.isSuccess());
        }

        // Vote rejected as follower's logs are ahead of the candidate's logs.
        {
            long expectedEpoch = 2;
            RequestVoteRequest request = new RequestVoteRequest(expectedEpoch, -1, -1);
            peer1RaftNode.rpcHandler(new RaftEnvelope(peer3, peer1, "2", "Test-Shard", request));
            List<RaftEnvelope> envelopes = transport.drainEnvelopes();

            Assertions.assertEquals(1, envelopes.size());
            Assertions.assertInstanceOf(RequestVoteResponse.class, envelopes.getFirst().getMessage());
            RequestVoteResponse response = (RequestVoteResponse) envelopes.getFirst().getMessage();
            Assertions.assertEquals(expectedEpoch, response.getEpoch());
            Assertions.assertFalse(response.isVoteGranted());
        }
    }
}
