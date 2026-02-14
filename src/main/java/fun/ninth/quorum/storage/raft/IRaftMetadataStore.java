package fun.ninth.quorum.storage.raft;

import org.jetbrains.annotations.Nullable;

import fun.ninth.quorum.raft.models.RaftMetadata;

public interface IRaftMetadataStore {
    void save(RaftMetadata metadata);

    @Nullable RaftMetadata load();
}
