package fun.ninth.quorum.storage.raft;

import fun.ninth.quorum.raft.models.RaftMetadata;

/// Stores the raft's metadata in memory. Right now only used for testing.
public class InMemoryRaftMetadataStore implements IRaftMetadataStore {
    private RaftMetadata metadata;

    @Override
    public void save(RaftMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public RaftMetadata load() {
        return metadata;
    }
}
