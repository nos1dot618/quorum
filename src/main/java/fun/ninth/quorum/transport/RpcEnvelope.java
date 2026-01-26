package fun.ninth.quorum.transport;

import com.fasterxml.jackson.databind.JsonNode;

import fun.ninth.quorum.node.NodeId;
import fun.ninth.quorum.transport.requests.RpcMessageType;

public class RpcEnvelope {
    private NodeId sourceNodeId;
    private NodeId destinationNodeId;
    private RpcMessageType messageType;
    private String requestId;
    private String groupId;
    /// Replication-Group/Shard ID.
    private long epoch;
    /// Term
    private JsonNode payload;

    /// No-Argument-Constructor and setters are required for jackson-deserializing.
    public RpcEnvelope() {
    }

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

    public void setSourceNodeId(NodeId sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public NodeId getDestinationNodeId() {
        return destinationNodeId;
    }

    public void setDestinationNodeId(NodeId destinationNodeId) {
        this.destinationNodeId = destinationNodeId;
    }

    public RpcMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(RpcMessageType messageType) {
        this.messageType = messageType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }
}
