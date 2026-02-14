package fun.ninth.quorum.storage.raft;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import fun.ninth.quorum.raft.models.RaftMetadata;

public class JsonRaftMetadataStore implements IRaftMetadataStore {

    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonRaftMetadataStore(String path) {
        file = new File(path);
    }

    @Override
    public void save(RaftMetadata metadata) {
        try {
            mapper.writeValue(file, metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RaftMetadata load() {
        if (!file.exists()) return null;

        try {
            return mapper.readValue(file, RaftMetadata.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
