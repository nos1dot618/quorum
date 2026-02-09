package fun.ninth.quorum.raft.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class RequestVoteResponse implements IRaftMessage {
    private final long epoch;
    private final boolean voteGranted;

    @JsonCreator
    public RequestVoteResponse(@JsonProperty("epoch") long epoch, @JsonProperty("voteGranted") boolean voteGranted) {
        this.epoch = epoch;
        this.voteGranted = voteGranted;
    }

    @SuppressWarnings("unused")
    public long getEpoch() {
        return epoch;
    }

    @SuppressWarnings("unused")
    public boolean isVoteGranted() {
        return voteGranted;
    }
}
