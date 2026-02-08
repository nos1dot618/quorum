package fun.ninth.quorum.raft.messages;

import fun.ninth.quorum.raft.Ledger;

public final class AppendEntriesRequest implements IRaftMessage {
    /// Current Term
    private long epoch;
    private long previousEntryIndex;
    private long previousEntryEpoch;
    private Ledger ledger;
    private long leaderCommitIndex;

    @SuppressWarnings("unused")
    public AppendEntriesRequest() {
    }

    public AppendEntriesRequest(long epoch, long previousEntryIndex, long previousEntryEpoch, Ledger ledger, long leaderCommitIndex) {
        this.epoch = epoch;
        this.previousEntryIndex = previousEntryIndex;
        this.previousEntryEpoch = previousEntryEpoch;
        this.ledger = ledger;
        this.leaderCommitIndex = leaderCommitIndex;
    }

    @SuppressWarnings("unused")
    public long getPreviousEntryIndex() {
        return previousEntryIndex;
    }

    @SuppressWarnings("unused")
    public void setPreviousEntryIndex(long previousEntryIndex) {
        this.previousEntryIndex = previousEntryIndex;
    }

    @SuppressWarnings("unused")
    public long getPreviousEntryEpoch() {
        return previousEntryEpoch;
    }

    @SuppressWarnings("unused")
    public void setPreviousEntryEpoch(long previousEntryEpoch) {
        this.previousEntryEpoch = previousEntryEpoch;
    }

    @SuppressWarnings("unused")
    public Ledger getLedger() {
        return ledger;
    }

    @SuppressWarnings("unused")
    public void setLedger(Ledger ledger) {
        this.ledger = ledger;
    }

    @SuppressWarnings("unused")
    public long getLeaderCommitIndex() {
        return leaderCommitIndex;
    }

    @SuppressWarnings("unused")
    public void setLeaderCommitIndex(long leaderCommitIndex) {
        this.leaderCommitIndex = leaderCommitIndex;
    }

    @SuppressWarnings("unused")
    public long getEpoch() {
        return epoch;
    }

    @SuppressWarnings("unused")
    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }
}
