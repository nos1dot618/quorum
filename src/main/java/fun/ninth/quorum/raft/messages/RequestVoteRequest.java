package fun.ninth.quorum.raft.messages;

public final class RequestVoteRequest implements IRaftMessage {
    private long epoch;
    private long previousEntryIndex;
    private long previousEntryEpoch;

    @SuppressWarnings("unused")
    public RequestVoteRequest() {
    }

    public RequestVoteRequest(long epoch, long previousEntryIndex, long previousEntryEpoch) {
        this.epoch = epoch;
        this.previousEntryIndex = previousEntryIndex;
        this.previousEntryEpoch = previousEntryEpoch;
    }

    @SuppressWarnings("unused")
    public long getEpoch() {
        return epoch;
    }

    @SuppressWarnings("unused")
    public void setEpoch(long epoch) {
        this.epoch = epoch;
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
}
