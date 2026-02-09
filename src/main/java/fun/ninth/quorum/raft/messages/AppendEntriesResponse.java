package fun.ninth.quorum.raft.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AppendEntriesResponse implements IRaftMessage {
    private final long epoch;
    private final boolean success;
    private final long nextEntryIndex;
    private final Long conflictEpoch;

    @JsonCreator
    public AppendEntriesResponse(@JsonProperty("epoch") long epoch, @JsonProperty("success") boolean success,
                                 @JsonProperty("nextEntryIndex") long nextEntryIndex,
                                 @JsonProperty("conflictEpoch") Long conflictEpoch)
    {
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
    public long getNextEntryIndex() {
        return nextEntryIndex;
    }

    @SuppressWarnings("unused")
    public Long getConflictEpoch() {
        return conflictEpoch;
    }

    @SuppressWarnings("unused")
    public long getEpoch() {
        return epoch;
    }
}
