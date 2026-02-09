package fun.ninth.quorum.raft;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.raft.messages.IRaftMessage;
import fun.ninth.quorum.transport.RpcEnvelope;

public class RaftEnvelope extends RpcEnvelope {
    private final Peer sourcePeer;
    private final Peer destinationPeer;
    private final String requestId;
    /// Replication-Group/Shard ID.
    private final String groupId;
    private final IRaftMessage message;

    @JsonCreator
    public RaftEnvelope(@JsonProperty("sourcePeer") Peer sourcePeer,
                        @JsonProperty("destinationPeer") Peer destinationPeer,
                        @JsonProperty("requestId") String requestId, @JsonProperty("groupId") String groupId,
                        @JsonProperty("message") IRaftMessage message)
    {
        this.sourcePeer = sourcePeer;
        this.destinationPeer = destinationPeer;
        this.requestId = requestId;
        this.groupId = groupId;
        this.message = message;
    }

    @SuppressWarnings("unused")
    public Peer getSourcePeer() {
        return sourcePeer;
    }

    @SuppressWarnings("unused")
    public Peer getDestinationPeer() {
        return destinationPeer;
    }

    @SuppressWarnings("unused")
    public IRaftMessage getMessage() {
        return message;
    }

    @SuppressWarnings("unused")
    public String getRequestId() {
        return requestId;
    }

    @SuppressWarnings("unused")
    public String getGroupId() {
        return groupId;
    }
}
