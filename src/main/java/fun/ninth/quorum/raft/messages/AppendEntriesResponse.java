package fun.ninth.quorum.raft.messages;

public final class AppendEntriesResponse implements IRaftMessage {
    private long epoch;
    private boolean success;
    private long nextEntryIndex;
    private Long conflictEpoch;

    @SuppressWarnings("unused")
    public AppendEntriesResponse() {
    }

    public AppendEntriesResponse(long epoch, boolean success, long nextEntryIndex, Long conflictEpoch) {
        this.epoch = epoch;
        this.success = success;
        this.nextEntryIndex = nextEntryIndex;
        this.conflictEpoch = conflictEpoch;
    }

    @SuppressWarnings("unused")
    public boolean isSuccess() {
        return success;
    }

    @SuppressWarnings("unused")
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @SuppressWarnings("unused")
    public long getNextEntryIndex() {
        return nextEntryIndex;
    }

    @SuppressWarnings("unused")
    public void setNextEntryIndex(long nextEntryIndex) {
        this.nextEntryIndex = nextEntryIndex;
    }

    @SuppressWarnings("unused")
    public Long getConflictEpoch() {
        return conflictEpoch;
    }

    @SuppressWarnings("unused")
    public void setConflictEpoch(Long conflictEpoch) {
        this.conflictEpoch = conflictEpoch;
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
