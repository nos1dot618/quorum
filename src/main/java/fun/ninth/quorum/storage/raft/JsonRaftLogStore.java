package fun.ninth.quorum.storage.raft;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import fun.ninth.quorum.raft.logs.Ledger;
import fun.ninth.quorum.raft.logs.LogEntry;

public class JsonRaftLogStore implements IRaftLogStore {
    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonRaftLogStore(String path) {
        file = new File(path);
    }

    @Override
    public Ledger load() {
        if (!file.exists()) return new Ledger();

        try {
            return mapper.readValue(file, Ledger.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Ledger ledger) {
        try {
            mapper.writeValue(file, ledger);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void append(Ledger ledger, LogEntry logEntry) {
        ledger.add(logEntry);
        save(ledger);
    }

    @Override
    public void appendAll(Ledger ledger, Ledger other) {
        ledger.addAll(other);
        save(ledger);
    }

    @Override
    public void truncateFrom(Ledger ledger, long index) {
        while (ledger.size() > index) ledger.removeLast();
        save(ledger);
    }
}
