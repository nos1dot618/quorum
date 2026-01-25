package fun.ninth.quorum.transport;

import fun.ninth.quorum.node.NodeId;
import fun.ninth.quorum.transport.requests.RpcMessageType;

import com.fasterxml.jackson.databind.JsonNode;

public class RpcEnvelope {
    private final NodeId sourceNodeId;
    private final NodeId destinationNodeId;
    private final RpcMessageType messageType;
    private final String requestId;
    private final String groupId; // Replication-Group/Shard ID.
    private final long epoch; // Term
    private final JsonNode payload;

    public RpcEnvelope(NodeId sourceNodeId, NodeId destinationNodeId, RpcMessageType messageType, String requestId, String groupId, long epoch, JsonNode payload) {
        this.sourceNodeId = sourceNodeId;
        this.destinationNodeId = destinationNodeId;
        this.messageType = messageType;
        this.requestId = requestId;
        this.groupId = groupId;
        this.epoch = epoch;
        this.payload = payload;
    }

    public NodeId getSourceNodeId() {
        return sourceNodeId;
    }

    public NodeId getDestinationNodeId() {
        return destinationNodeId;
    }

    public RpcMessageType getMessageType() {
        return messageType;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getGroupId() {
        return groupId;
    }

    public long getEpoch() {
        return epoch;
    }

    public JsonNode getPayload() {
        return payload;
    }
}
