package fun.ninth.quorum.transport.requests;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import fun.ninth.quorum.transport.RpcEnvelope;

public class AppendEntriesRequest {
    private long previousEntryIndex;
    private long previousEntryEpoch;
    private List<String> entries;
    private long leaderCommit;

    public AppendEntriesRequest() {
    }

    public AppendEntriesRequest(long previousEntryIndex, long previousEntryEpoch, List<String> entries, long leaderCommit) {
        this.previousEntryIndex = previousEntryIndex;
        this.previousEntryEpoch = previousEntryEpoch;
        this.entries = entries;
        this.leaderCommit = leaderCommit;
    }

    public static void handleRpcEnvelope(RpcEnvelope envelope) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AppendEntriesRequest request = mapper.treeToValue(envelope.getPayload(), AppendEntriesRequest.class);
            System.out.println("Append-Entries received from " + envelope.getSourceNodeId().getId() + ", epoch: " + envelope.getEpoch());
            System.out.println("Entries: " + request.getEntries());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getPreviousEntryIndex() {
        return previousEntryIndex;
    }

    public void setPreviousEntryIndex(long previousEntryIndex) {
        this.previousEntryIndex = previousEntryIndex;
    }

    public long getPreviousEntryEpoch() {
        return previousEntryEpoch;
    }

    public void setPreviousEntryEpoch(long previousEntryEpoch) {
        this.previousEntryEpoch = previousEntryEpoch;
    }

    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }

    public long getLeaderCommit() {
        return leaderCommit;
    }

    public void setLeaderCommit(long leaderCommit) {
        this.leaderCommit = leaderCommit;
    }
}
