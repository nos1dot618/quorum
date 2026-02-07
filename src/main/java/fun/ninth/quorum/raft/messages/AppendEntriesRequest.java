package fun.ninth.quorum.raft.messages;

import java.util.List;

public final class AppendEntriesRequest implements IRaftMessage {
    /// Current Term
    private long epoch;
    private long previousEntryIndex;
    private long previousEntryEpoch;
    private List<String> entries;
    private long leaderCommitIndex;

    @SuppressWarnings("unused")
    public AppendEntriesRequest() {
    }

    public AppendEntriesRequest(long epoch, long previousEntryIndex, long previousEntryEpoch, List<String> entries, long leaderCommitIndex) {
        this.epoch = epoch;
        this.previousEntryIndex = previousEntryIndex;
        this.previousEntryEpoch = previousEntryEpoch;
        this.entries = entries;
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
    public List<String> getEntries() {
        return entries;
    }

    @SuppressWarnings("unused")
    public void setEntries(List<String> entries) {
        this.entries = entries;
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
