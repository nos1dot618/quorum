package fun.ninth.quorum.storage.raft;

import fun.ninth.quorum.raft.logs.Ledger;
import fun.ninth.quorum.raft.logs.LogEntry;

/// Stores the raft's logs in memory. Right now only used for testing.
public class InMemoryRaftLogStore implements IRaftLogStore {
    private Ledger ledger = new Ledger();

    @Override
    public Ledger load() {
        return ledger;
    }

    @Override
    public void save(Ledger ledger) {
        this.ledger = ledger;
    }

    @Override
    public void append(Ledger ledger, LogEntry logEntry) {
        save(ledger);
        this.ledger.add(logEntry);
    }

    @Override
    public void appendAll(Ledger ledger, Ledger other) {
        save(ledger);
        this.ledger.addAll(other);
    }

    @Override
    public void truncateFrom(Ledger ledger, long index) {
        save(ledger);
        while (ledger.size() > index) ledger.removeLast();
    }
}
