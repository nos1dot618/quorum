package fun.ninth.quorum.raft;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fun.ninth.quorum.node.NodeId;
import fun.ninth.quorum.raft.messages.AppendEntriesRequest;
import fun.ninth.quorum.raft.messages.AppendEntriesResponse;
import fun.ninth.quorum.raft.messages.RequestVoteRequest;
import fun.ninth.quorum.raft.messages.RequestVoteResponse;

public class RaftNode {
    private final NodeId nodeId;
    private final IRaftTransport transport;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Persistent raft state.
    private long currentEpoch = 0;
    private NodeId votedFor = null;
    // TODO: Choose a better implementation
    private List<String> logEntries = new ArrayList<>();

    // Volatile raft state
    private long commitIndex = -1;
    private long lastApplied = -1;
    private RaftRole role = RaftRole.FOLLOWER;

    public RaftNode(NodeId nodeId, IRaftTransport transport) {
        this.nodeId = nodeId;
        this.transport = transport;
    }

    public void rpcHandler(RaftEnvelope envelope) {
        executorService.submit(() -> {
            switch (envelope.getMessage()) {
                case AppendEntriesRequest request -> handleAppendEntriesRequest(envelope, request);
                case AppendEntriesResponse response -> handleAppendEntriesResponse(envelope, response);
                case RequestVoteRequest requestVoteRequest -> {
                }
                case RequestVoteResponse requestVoteResponse -> {
                }
            }
        });

    }

    private void handleAppendEntriesRequest(RaftEnvelope envelope, AppendEntriesRequest request) {
        //  Epoch check.
        if (request.getEpoch() < currentEpoch) {
            replyAppendEntries(envelope, false, logEntries.size(), null);
            return;
        }
        // Latest entries are present, thus become a follower.
        if (request.getEpoch() > currentEpoch) {
            currentEpoch = request.getEpoch();
            role = RaftRole.FOLLOWER;
            votedFor = null;
        }
        // Leader sent the heartbeat, stay a follower.
        role = RaftRole.FOLLOWER;
        // Missing entries.
        if (request.getPreviousEntryIndex() >= 0 && request.getPreviousEntryIndex() >= logEntries.size()) {
            replyAppendEntries(envelope, false, logEntries.size(), null);
            return;
        }
        // Remove conflicting entries.
        int index = (int) request.getPreviousEntryIndex() + 1;
        while (logEntries.size() > index) {
            logEntries.removeLast();
        }
        // Not a heartbeat.
        if (request.getEntries() != null && !request.getEntries().isEmpty()) {
            logEntries.addAll(request.getEntries());
        }
        // Update commit index.
        if (request.getLeaderCommitIndex() > commitIndex) {
            commitIndex = Math.min(request.getLeaderCommitIndex(), logEntries.size());
        }
        replyAppendEntries(envelope, true, logEntries.size(), null);
    }

    private void replyAppendEntries(RaftEnvelope envelope, boolean success, long nextEntryIndex, @SuppressWarnings("SameParameterValue") Long conflictEpoch) {
        AppendEntriesResponse response = new AppendEntriesResponse(success, nextEntryIndex, conflictEpoch);
        transport.send(envelope.getSourcePeer(), response);
    }

    private void handleAppendEntriesResponse(RaftEnvelope envelope, AppendEntriesResponse response) {
        if (role != RaftRole.LEADER) return;
        if (!response.isSuccess()) {
            // TODO: Send missing log entries to the follower.
        }
        // TODO: Successful replication, increment the successCount, wait till majority before commiting.
    }
}
