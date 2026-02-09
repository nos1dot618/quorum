package fun.ninth.quorum.raft.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class RequestVoteRequest implements IRaftMessage {
    private final long epoch;
    private final long previousEntryIndex;
    private final long previousEntryEpoch;

    @JsonCreator
    public RequestVoteRequest(@JsonProperty("epoch") long epoch,
                              @JsonProperty("previousEntryIndex") long previousEntryIndex,
                              @JsonProperty("previousEntryEpoch") long previousEntryEpoch)
    {
        this.epoch = epoch;
        this.previousEntryIndex = previousEntryIndex;
        this.previousEntryEpoch = previousEntryEpoch;
    }

    @SuppressWarnings("unused")
    public long getEpoch() {
        return epoch;
    }

    @SuppressWarnings("unused")
    public long getPreviousEntryIndex() {
        return previousEntryIndex;
    }

    @SuppressWarnings("unused")
    public long getPreviousEntryEpoch() {
        return previousEntryEpoch;
    }
}
