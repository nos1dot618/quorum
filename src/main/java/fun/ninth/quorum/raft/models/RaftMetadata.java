package fun.ninth.quorum.raft.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fun.ninth.quorum.node.NodeId;

public class RaftMetadata {
    private final long epoch;
    private final NodeId votedFor;

    @JsonCreator
    public RaftMetadata(@JsonProperty("epoch") long epoch, @JsonProperty("votedFor") NodeId votedFor) {
        this.epoch = epoch;
        this.votedFor = votedFor;
    }

    @SuppressWarnings("unused")
    public long getEpoch() {
        return epoch;
    }

    @SuppressWarnings("unused")
    public NodeId getVotedFor() {
        return votedFor;
    }
}
