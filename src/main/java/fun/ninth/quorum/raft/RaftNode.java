package fun.ninth.quorum.raft;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.node.NodeId;
import fun.ninth.quorum.raft.logs.Ledger;
import fun.ninth.quorum.raft.logs.LogEntry;
import fun.ninth.quorum.raft.messages.AppendEntriesRequest;
import fun.ninth.quorum.raft.messages.AppendEntriesResponse;
import fun.ninth.quorum.raft.messages.RequestVoteRequest;
import fun.ninth.quorum.raft.messages.RequestVoteResponse;
import fun.ninth.quorum.raft.transport.IRaftTransport;

public class RaftNode {
    private static final long ELECTION_TIMEOUT_MIN_MS = 150;
    private static final long ELECTION_TIMEOUT_MAX_MS = 300;
    private static final long HEARTBEAT_TIMEOUT_MS = 50;

    private final Peer peer;

    // Lifecycle.
    private final IRaftTransport transport;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> electionTimeoutTask;
    private ScheduledFuture<?> heartbeatTask;

    // Persistent raft state.
    private long currentEpoch = 0;
    private NodeId votedFor = null;
    private final Ledger ledger = new Ledger();

    // Volatile raft state.
    private long commitIndex = -1;
    private RaftRole role = RaftRole.FOLLOWER;

    // Leader state.
    private final Map<NodeId, Integer> nextIndexMap = new HashMap<>();
    private final Map<NodeId, Integer> matchIndexMap = new HashMap<>();

    // Candidate state.
    private int voteCount = 0;

    public RaftNode(Peer peer, IRaftTransport transport) {
        this.peer = peer;
        this.transport = transport;
        executorService = Executors.newSingleThreadExecutor();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /// Right now only used for testing.
    public RaftNode(Peer peer, IRaftTransport transport, ExecutorService executorService,
                    ScheduledExecutorService scheduler)
    {
        this.peer = peer;
        this.transport = transport;
        this.executorService = executorService;
        this.scheduler = scheduler == null ? Executors.newSingleThreadScheduledExecutor() : scheduler;
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

    public void start() {
        resetElectionTimeout();
    }

    public void stop() {
        scheduler.shutdown();
    }

    public RaftRole getRole() {
        return role;
    }

    public long getCommitIndex() {
        return commitIndex;
    }

    ///  Adds the command to the leader's ledger.
    ///
    /// @throws IllegalStateException if the node is the leader.
    public void propose(Object command) {
        executorService.submit(() -> {
            if (role != RaftRole.LEADER) throw new IllegalStateException("Node is not the leader");
            LogEntry entry = new LogEntry(currentEpoch, command);
            ledger.add(entry);
            // Optimistically try to replicate immediately, do not wait for next heartbeat task.
            sendHeartbeats();
        });
    }

    private void handleAppendEntriesRequest(RaftEnvelope envelope, AppendEntriesRequest request) {
        if (request.getEpoch() < currentEpoch) {
            replyAppendEntries(envelope, false, ledger.size(), null);
            return;
        }
        // Latest entries are present or leader sent a heartbeat, thus become a follower.
        if (request.getEpoch() > currentEpoch) becomeFollower(request.getEpoch());
        resetElectionTimeout();
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
        stopHeartbeatLoop();
        currentEpoch = newEpoch;
        role = RaftRole.FOLLOWER;
        votedFor = null;
        voteCount = 0;
        nextIndexMap.clear();
        matchIndexMap.clear();
    }

    private void replyAppendEntries(RaftEnvelope envelope, boolean success, long nextEntryIndex,
                                    @SuppressWarnings("SameParameterValue") Long conflictEpoch)
    {
        AppendEntriesResponse response = new AppendEntriesResponse(currentEpoch,
                                                                   success,
                                                                   nextEntryIndex,
                                                                   conflictEpoch);
        transport.send(peer, envelope.getSourcePeer(), response);
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

    private void sendAppendEntries(Peer destinationPeer) {
        // Default request index is 0.
        int nextIndex = nextIndexMap.getOrDefault(destinationPeer.getNodeId(), 0);
        int previousEntryIndex = nextIndex - 1;
        Ledger subLedger = ledger.subLedger(nextIndex, ledger.size());
        AppendEntriesRequest request = new AppendEntriesRequest(currentEpoch,
                                                                previousEntryIndex,
                                                                ledger.getEpoch(previousEntryIndex),
                                                                subLedger,
                                                                commitIndex);
        transport.send(peer, destinationPeer, request);
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
        boolean logUpToDate = true;
        if (!ledger.isEmpty()) logUpToDate = request.getPreviousEntryEpoch() > ledger.getLast().getEpoch() ||
                (request.getPreviousEntryEpoch() == ledger.getLast().getEpoch() &&
                        request.getPreviousEntryIndex() >= ledger.size() - 1);
        if (canVote && logUpToDate && request.getEpoch() == currentEpoch) {
            votedFor = envelope.getSourcePeer().getNodeId();
            voteGranted = true;
        }
        // In all the other cases the candidate is behind the voter, thus voteGranted remains false.
        RequestVoteResponse response = new RequestVoteResponse(currentEpoch, voteGranted);
        transport.send(peer, envelope.getSourcePeer(), response);
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
        for (var destinationPeer : transport.getPeers()) {
            if (peer.equals(destinationPeer)) continue;
            nextIndexMap.put(destinationPeer.getNodeId(), ledger.size());
            matchIndexMap.put(destinationPeer.getNodeId(), -1);
            sendAppendEntries(destinationPeer);
        }
        startHeartbeatLoop();
    }

    private void becomeCandidate() {
        stopHeartbeatLoop();
        role = RaftRole.CANDIDATE;
        currentEpoch++;
        votedFor = peer.getNodeId();
        voteCount = 1;
        resetElectionTimeout();

        LogEntry previousEntry = ledger.getLast();
        long previousEntryIndex = ledger.size() - 1;
        long previousEntryEpoch = previousEntry != null ? previousEntry.getEpoch() : -1;
        RequestVoteRequest request = new RequestVoteRequest(currentEpoch, previousEntryIndex, previousEntryEpoch);
        for (var destinationPeer : transport.getPeers()) {
            if (peer.equals(destinationPeer)) continue;
            transport.send(peer, destinationPeer, request);
        }
    }

    private void onElectionTimeout() {
        if (role == RaftRole.LEADER) return;
        becomeCandidate();
    }

    private long randomElectionTimeout() {
        return ThreadLocalRandom.current().nextLong(ELECTION_TIMEOUT_MIN_MS, ELECTION_TIMEOUT_MAX_MS);
    }

    private void resetElectionTimeout() {
        // Cancel the scheduled task if not running.
        if (electionTimeoutTask != null) electionTimeoutTask.cancel(false);
        electionTimeoutTask = scheduler.schedule(() -> executorService.submit(this::onElectionTimeout),
                                                 randomElectionTimeout(),
                                                 TimeUnit.MILLISECONDS);
    }

    private void sendHeartbeats() {
        if (role != RaftRole.LEADER) return;
        for (var destinationPeer : transport.getPeers()) {
            if (peer.equals(destinationPeer)) continue;
            sendAppendEntries(destinationPeer);
        }
    }

    private void startHeartbeatLoop() {
        // Cancel the scheduled task if not running.
        if (heartbeatTask != null) heartbeatTask.cancel(false);
        heartbeatTask = scheduler.scheduleAtFixedRate(() -> executorService.submit(this::sendHeartbeats),
                                                      0,
                                                      HEARTBEAT_TIMEOUT_MS,
                                                      TimeUnit.MILLISECONDS);
    }

    private void stopHeartbeatLoop() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(false);
            heartbeatTask = null;
        }
    }
}
