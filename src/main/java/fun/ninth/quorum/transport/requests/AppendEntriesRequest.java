package fun.ninth.quorum.transport.requests;

import java.util.List;

public class AppendEntriesRequest {
    private final long previousEntryIndex;
    private final long previousEntryEpoch;
    private final List<String> entries;
    private final long leaderCommit;

    public AppendEntriesRequest(long previousEntryIndex, long previousEntryEpoch, List<String> entries, long leaderCommit) {
        this.previousEntryIndex = previousEntryIndex;
        this.previousEntryEpoch = previousEntryEpoch;
        this.entries = entries;
        this.leaderCommit = leaderCommit;
    }

    public long getPreviousEntryIndex() {
        return previousEntryIndex;
    }

    public long getPreviousEntryEpoch() {
        return previousEntryEpoch;
    }

    public List<String> getEntries() {
        return entries;
    }

    public long getLeaderCommit() {
        return leaderCommit;
    }
}
