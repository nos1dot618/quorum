package fun.ninth.quorum.raft;

import fun.ninth.quorum.cluster.Peer;
import fun.ninth.quorum.raft.messages.IRaftMessage;
import fun.ninth.quorum.transport.RpcEnvelope;

public class RaftEnvelope extends RpcEnvelope {
    private Peer sourcePeer;
    private Peer destinationPeer;
    private String requestId;
    /// Replication-Group/Shard ID.
    private String groupId;
    private IRaftMessage message;

    @SuppressWarnings("unused")
    public RaftEnvelope() {
    }

    public RaftEnvelope(Peer sourcePeer, Peer destinationPeer, String requestId, String groupId, IRaftMessage message) {
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
    public void setSourcePeer(Peer sourcePeer) {
        this.sourcePeer = sourcePeer;
    }

    @SuppressWarnings("unused")
    public Peer getDestinationPeer() {
        return destinationPeer;
    }

    @SuppressWarnings("unused")
    public void setDestinationPeer(Peer destinationPeer) {
        this.destinationPeer = destinationPeer;
    }

    @SuppressWarnings("unused")
    public IRaftMessage getMessage() {
        return message;
    }

    @SuppressWarnings("unused")
    public void setMessage(IRaftMessage message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    public String getRequestId() {
        return requestId;
    }

    @SuppressWarnings("unused")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @SuppressWarnings("unused")
    public String getGroupId() {
        return groupId;
    }

    @SuppressWarnings("unused")
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
