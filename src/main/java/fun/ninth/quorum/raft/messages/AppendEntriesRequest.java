package fun.ninth.quorum.raft.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fun.ninth.quorum.raft.logs.Ledger;

public final class AppendEntriesRequest implements IRaftMessage {
    /// Current Term
    private final long epoch;
    private final long previousEntryIndex;
    private final long previousEntryEpoch;
    private final Ledger ledger;
    private final long leaderCommitIndex;

    @JsonCreator
    public AppendEntriesRequest(@JsonProperty("epoch") long epoch,
                                @JsonProperty("previousEntryIndex") long previousEntryIndex,
                                @JsonProperty("previousEntryEpoch") long previousEntryEpoch,
                                @JsonProperty("ledger") Ledger ledger,
                                @JsonProperty("leaderCommitIndex") long leaderCommitIndex)
    {
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
    public long getPreviousEntryEpoch() {
        return previousEntryEpoch;
    }

    @SuppressWarnings("unused")
    public Ledger getLedger() {
        return ledger;
    }

    @SuppressWarnings("unused")
    public long getLeaderCommitIndex() {
        return leaderCommitIndex;
    }

    @SuppressWarnings("unused")
    public long getEpoch() {
        return epoch;
    }
}
