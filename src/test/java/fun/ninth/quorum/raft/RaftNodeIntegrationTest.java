package fun.ninth.quorum.raft;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.node.NodeId;
import fun.ninth.quorum.raft.transport.InMemoryRaftTransport;
import fun.ninth.quorum.utils.DirectExecutorService;

public class RaftNodeIntegrationTest {
    private static final int WAIT_TIME_MS = 400;

    private InMemoryRaftTransport transport;

    @BeforeEach
    void setup() {
        transport = new InMemoryRaftTransport();
        DirectExecutorService executorService = new DirectExecutorService();
        Peer peer1 = new RaftPeer(new NodeId("1"), 9001);
        Peer peer2 = new RaftPeer(new NodeId("2"), 9002);
        Peer peer3 = new RaftPeer(new NodeId("3"), 9003);
        RaftNode peer1RaftNode = new RaftNode(peer1, transport, executorService, null);
        RaftNode peer2RaftNode = new RaftNode(peer2, transport, executorService, null);
        RaftNode peer3RaftNode = new RaftNode(peer3, transport, executorService, null);
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
    void exactlyOneLeaderElectedInThreeNodeCluster() throws Exception {
        Thread.sleep(WAIT_TIME_MS); // Wait for leader to be established.
        AtomicInteger leaderCount = new AtomicInteger(0);
        transport.forEachRaftNode(raftNode -> {
            if (raftNode.getRole() == RaftRole.LEADER) leaderCount.incrementAndGet();
        });

        Assertions.assertEquals(1, leaderCount.get());
    }

    @Test
    void commitIndexAdvancesOnlyAfterMajorityReplication() throws Exception {
        Thread.sleep(WAIT_TIME_MS); // Wait for leader to be established.
        transport.forEachRaftNode(node -> {
            if (node.getRole() != RaftRole.LEADER) return;
            node.propose("some command");
        });

        Thread.sleep(WAIT_TIME_MS); // Wait for replication.
        AtomicLong commitIndex = new AtomicLong(-1);
        transport.forEachRaftNode(node -> {
            if (node.getRole() != RaftRole.LEADER) return;
            commitIndex.set(node.getCommitIndex());
        });

        Assertions.assertEquals(0, commitIndex.get());
    }
}
