package fun.ninth.quorum.raft;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.node.NodeId;
import fun.ninth.quorum.raft.messages.AppendEntriesRequest;
import fun.ninth.quorum.raft.messages.AppendEntriesResponse;
import fun.ninth.quorum.raft.messages.RequestVoteRequest;
import fun.ninth.quorum.raft.messages.RequestVoteResponse;

public class RaftNode {
    private final IRaftTransport transport;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Persistent raft state.
    private long currentEpoch = 0;
    private NodeId votedFor = null;
    // TODO: Choose a better implementation
    private final Ledger ledger = new Ledger();

    // Volatile raft state.
    private long commitIndex = -1;
    private RaftRole role = RaftRole.FOLLOWER;

    // Leader state.
    private final Map<NodeId, Integer> nextIndexMap = new HashMap<>();
    private final Map<NodeId, Integer> matchIndexMap = new HashMap<>();

    // Candidate state.
    private int voteCount = 0;

    public RaftNode(IRaftTransport transport) {
        this.transport = transport;
    }

    public void rpcHandler(RaftEnvelope envelope) {
        executorService.submit(() -> {
            switch (envelope.getMessage()) {
                case AppendEntriesRequest request -> handleAppendEntriesRequest(envelope, request);
                case AppendEntriesResponse response -> handleAppendEntriesResponse(envelope, response);
                case RequestVoteRequest request -> handleRequestVoteRequest(envelope, request);
                case RequestVoteResponse response -> handleRequestVoteResponse(envelope, response);
            }
        });
    }

    private void handleAppendEntriesRequest(RaftEnvelope envelope, AppendEntriesRequest request) {
        if (request.getEpoch() < currentEpoch) {
            replyAppendEntries(envelope, false, ledger.size(), null);
            return;
        }
        // Latest entries are present, thus become a follower.
        if (request.getEpoch() > currentEpoch) becomeFollower(request.getEpoch());
        // Leader sent the heartbeat, stay a follower.
        role = RaftRole.FOLLOWER;
        // Missing entries.
        if (request.getPreviousEntryIndex() >= ledger.size()) {
            replyAppendEntries(envelope, false, ledger.size(), null);
            return;
        }
        // Remove conflicting entries.
        int index = (int) request.getPreviousEntryIndex() + 1;
        while (ledger.size() > index) {
            ledger.removeLast();
        }
        // Not a heartbeat.
        if (request.getLedger() != null && !request.getLedger().isEmpty()) {
            ledger.addAll(request.getLedger());
        }
        // Update commit index.
        if (request.getLeaderCommitIndex() > commitIndex) {
            commitIndex = Math.min(request.getLeaderCommitIndex(), ledger.size() - 1);
        }
        replyAppendEntries(envelope, true, ledger.size(), null);
    }

    private void becomeFollower(long newEpoch) {
        currentEpoch = newEpoch;
        role = RaftRole.FOLLOWER;
        votedFor = null;
        voteCount = 0;
        nextIndexMap.clear();
        matchIndexMap.clear();
    }

    private void replyAppendEntries(RaftEnvelope envelope, boolean success, long nextEntryIndex, @SuppressWarnings("SameParameterValue") Long conflictEpoch) {
        AppendEntriesResponse response = new AppendEntriesResponse(currentEpoch, success, nextEntryIndex, conflictEpoch);
        transport.send(envelope.getSourcePeer(), response);
    }

    private void handleAppendEntriesResponse(RaftEnvelope envelope, AppendEntriesResponse response) {
        if (role != RaftRole.LEADER) return;
        if (response.getEpoch() > currentEpoch) becomeFollower(response.getEpoch());
        NodeId followerNodeId = envelope.getSourcePeer().getNodeId();
        if (!response.isSuccess()) {
            if (response.getConflictEpoch() != null) {
                int lastIndexOfEpoch = ledger.findLastIndexOfEpoch(response.getConflictEpoch());
                if (lastIndexOfEpoch >= 0) {
                    // Ledger has the epoch, skip directly to the next index.
                    nextIndexMap.put(followerNodeId, lastIndexOfEpoch + 1);
                } else {
                    // Ledger does not have this epoch.
                    nextIndexMap.put(followerNodeId, (int) response.getNextEntryIndex());
                }
            } else {
                // Pure index conflict, skip to the requested index.
                nextIndexMap.put(followerNodeId, (int) response.getNextEntryIndex());
            }
            sendAppendEntries(envelope.getSourcePeer());
        }
        int replicatedIndex = (int) response.getNextEntryIndex() - 1;
        matchIndexMap.put(followerNodeId, replicatedIndex);
        nextIndexMap.put(followerNodeId, replicatedIndex + 1);
        tryAdvanceCommitIndex();
    }

    private void sendAppendEntries(Peer peer) {
        int nextIndex = nextIndexMap.get(peer.getNodeId());
        int previousEntryIndex = nextIndex - 1;
        Ledger subLedger = ledger.subLedger(nextIndex, ledger.size());
        AppendEntriesRequest request = new AppendEntriesRequest(currentEpoch, previousEntryIndex,
                ledger.getEpoch(previousEntryIndex), subLedger, commitIndex);
        transport.send(peer, request);
    }

    private void tryAdvanceCommitIndex() {
        for (int index = ledger.size() - 1; index > commitIndex; index--) {
            int replicatedCount = 1; // Started from 1 by including the leader.
            for (int matchIndex : matchIndexMap.values()) {
                if (matchIndex >= index) replicatedCount++;
            }
            // Bump the commit index once reached to a majority for an index.
            if (replicatedCount >= transport.getMajorityCount()) {
                commitIndex = index;
                break;
            }
        }
    }

    private void handleRequestVoteRequest(RaftEnvelope envelope, RequestVoteRequest request) {
        boolean voteGranted = false;
        // Candidate's term is newer then the voter's term, thus become a follower.
        if (request.getEpoch() > currentEpoch) becomeFollower(request.getEpoch());
        boolean canVote = votedFor == null || votedFor.equals(envelope.getSourcePeer().getNodeId());
        boolean logUpToDate = request.getPreviousEntryIndex() >= ledger.size() - 1;
        if (canVote && logUpToDate && request.getEpoch() == currentEpoch) {
            votedFor = envelope.getSourcePeer().getNodeId();
            voteGranted = true;
        }
        // In all the other cases the candidate is behind the voter, thus voteGranted remains false.
        RequestVoteResponse response = new RequestVoteResponse(currentEpoch, voteGranted);
        transport.send(envelope.getSourcePeer(), response);
    }

    private void handleRequestVoteResponse(RaftEnvelope ignore, RequestVoteResponse response) {
        if (role != RaftRole.CANDIDATE) return;
        if (response.getEpoch() > currentEpoch) {
            becomeFollower(response.getEpoch());
            return;
        }
        if (response.isVoteGranted()) {
            voteCount++;
            if (voteCount >= transport.getMajorityCount()) becomeLeader();
        }
    }

    private void becomeLeader() {
        role = RaftRole.LEADER;
        for (var peer : transport.getPeers()) {
            nextIndexMap.put(peer.getNodeId(), ledger.size());
            nextIndexMap.put(peer.getNodeId(), -1);
            sendAppendEntries(peer);
        }
    }
}
