package fun.ninth.quorum.storage.raft;

import org.jetbrains.annotations.NotNull;

import fun.ninth.quorum.raft.logs.Ledger;
import fun.ninth.quorum.raft.logs.LogEntry;

public interface IRaftLogStore {
    @NotNull Ledger load();

    void save(Ledger ledger);

    void append(Ledger ledger, LogEntry logEntry);

    void appendAll(Ledger ledger, Ledger other);

    void truncateFrom(Ledger ledger, long index);
}
